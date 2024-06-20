import asyncio
import logging
import os
from aiohttp import ClientSession
from dotenv import load_dotenv
from aiogram import Bot, Dispatcher, types
from aiogram.types import Message, ReplyKeyboardMarkup, KeyboardButton, InlineKeyboardMarkup, InlineKeyboardButton, \
    CallbackQuery
from aiogram.filters import CommandStart, Command
from aiogram.fsm.context import FSMContext
from aiogram.fsm.state import StatesGroup, State
from aiogram.fsm.storage.memory import MemoryStorage

# Загрузить переменные среды из файла .env
load_dotenv()

# Инициализируйте бота с помощью токена из переменных среды.
bot = Bot(token=os.getenv('TOKEN'))

# Инициализируйте диспетчер с FSM и памятью.
dp = Dispatcher(storage=MemoryStorage())

# Определить кнопки для главного меню бота
button_tests = KeyboardButton(text='Тесты')
button_rating = KeyboardButton(text='Рейтинг')
button_results = KeyboardButton(text='Мои результаты')
button_help = KeyboardButton(text='Помощь')

# Создайте экземпляр ReplyKeyboardMarkup для главного меню.
main_keyboard = ReplyKeyboardMarkup(
    keyboard=[
        [button_tests],
        [button_rating],
        [button_results],
        [button_help]
    ],
    resize_keyboard=True
)


# Определить состояние FSM
class TestStates(StatesGroup):
    SELECTING_TEST = State()
    IN_TEST = State()
    SHOW_RESULT = State()


# Определите обработчик для команды /start.
@dp.message(CommandStart())
async def cmd_start(message: Message):
    await message.answer(
        "Привет! Я бот для прохождения тестов. Вы можете выбрать тест из списка и начать его прохождение.\n"
        "\n"
        "Для получения справки используйте кнопку 'Помощь'.",
        reply_markup=main_keyboard  # Отправьте клавиатуру главного меню с сообщением
    )


# Определите обработчик для команды /help.
@dp.message(Command('help'))
async def get_help(message: Message):
    await message.answer(
        "Привет! Я бот для прохождения тестов.\n"
        "\n"
        "📝 Я предлагаю тебе пройти увлекательные квизы и проверить свои знания!\n\n"
        "Что я могу:\n"
        "- Предлагаю выбрать тест из списка доступных.\n"
        "- Задаю вопросы поочередно и собираю ответы.\n"
        "- Показываю результаты теста сразу после его завершения.\n\n"
        "Для участия:\n"
        "- Выбери тест, который тебе интересен.\n"
        "- Ответь на вопросы, которые я задам.\n"
        "- Получи результаты и узнай, насколько хорошо ты справился!"
    )


# Определите обработчик для кнопки "Тесты".
@dp.message(lambda message: message.text == 'Тесты')
async def handle_tests_button(message: Message, state: FSMContext):
    async with ClientSession() as session:
        try:
            async with session.get('http://localhost:3333/admin/get/test') as response:
                if response.status == 200:
                    tests = await response.json()
                    logging.info(f"Полученные данные: {tests}")  # Логирование полного JSON-ответа

                    # Создание текста для списка тестов
                    tests_list = "\n\n".join(
                        [f"Название: {test['title']}\nОписание: {test['description']}" for test in tests])

                    # Создание инлайн-кнопок для каждого теста
                    buttons = [InlineKeyboardButton(text=test['title'], callback_data=f"test_{test['testId']}") for test
                               in tests]
                    inline_kb = InlineKeyboardMarkup(inline_keyboard=[buttons])

                    # Отправка сообщения со списком тестов и инлайн-кнопками
                    await message.answer(f"Список доступных тестов:\n\n{tests_list}", reply_markup=inline_kb)
                    await state.set_state(TestStates.SELECTING_TEST)
                else:
                    await message.answer("Произошла ошибка при загрузке списка тестов")
        except Exception as e:
            logging.error(f"Ошибка при выполнении запроса на бэкенд: {e}")
            await message.answer("Произошла ошибка при выполнении запроса на сервер")


# Обработчик для инлайн-кнопок выбора теста
@dp.callback_query(lambda callback_query: callback_query.data.startswith("test_"))
async def handle_test_selection(callback_query: CallbackQuery, state: FSMContext):
    test_id = callback_query.data.split("_")[1]

    async with ClientSession() as session:
        try:
            async with session.get(f'http://localhost:3333/admin/get/test') as response:
                if response.status == 200:
                    tests = await response.json()
                    selected_test = next((test for test in tests if str(test['testId']) == test_id), None)

                    if selected_test:
                        await state.update_data(test_id=test_id, current_question=0, answers=[],
                                                selected_test=selected_test)

                        await callback_query.message.answer(
                            f"Вы выбрали тест:\n\nНазвание: {selected_test['title']}\nОписание: {selected_test['description']}\nНачнем тестирование!"
                        )
                        await state.set_state(TestStates.IN_TEST)
                        await ask_question(callback_query.message, state)
                    else:
                        await callback_query.message.answer("Произошла ошибка при выборе теста")
                else:
                    await callback_query.message.answer("Произошла ошибка при загрузке списка тестов")
        except Exception as e:
            logging.error(f"Ошибка при выполнении запроса на бэкенд: {e}")
            await callback_query.message.answer("Произошла ошибка при выполнении запроса на сервер")
    await callback_query.answer()


# Функция для получения и отправки вопроса
async def ask_question(message: Message, state: FSMContext):
    data = await state.get_data()
    test_id = data['test_id']
    current_question = data['current_question']

    async with ClientSession() as session:
        try:
            async with session.get(f'http://localhost:3333/admin/get/question/{test_id}') as response:
                if response.status == 200:
                    questions = await response.json()
                    if current_question < len(questions):
                        question = questions[current_question]
                        await state.update_data(questions=questions)
                        buttons = [InlineKeyboardButton(text=option, callback_data=f"answer_{option}") for option in
                                   question['options']]
                        inline_kb = InlineKeyboardMarkup(inline_keyboard=[buttons])

                        await message.answer(f"Вопрос {current_question + 1}: {question['text']}",
                                             reply_markup=inline_kb)
                    else:
                        await show_result(message, state)
                else:
                    await message.answer("Произошла ошибка при загрузке вопросов теста")
        except Exception as e:
            logging.error(f"Ошибка при выполнении запроса на бэкенд: {e}")
            await message.answer("Произошла ошибка при выполнении запроса на сервер")


# Обработчик для инлайн-кнопок выбора ответа
@dp.callback_query(lambda callback_query: callback_query.data.startswith("answer_"))
async def handle_answer_selection(callback_query: CallbackQuery, state: FSMContext):
    answer = callback_query.data.split("_")[1]
    data = await state.get_data()
    answers = data['answers']
    answers.append(answer)
    current_question = data['current_question'] + 1
    await state.update_data(answers=answers, current_question=current_question)

    await callback_query.answer()
    await ask_question(callback_query.message, state)


# Функция для показа результата
async def show_result(message: Message, state: FSMContext):
    data = await state.get_data()
    answers = data['answers']
    questions = data['questions']

    correct_answers = sum(1 for i, question in enumerate(questions) if question['correct_answer'] == answers[i])
    total_questions = len(questions)
    score = (correct_answers / total_questions) * 100

    await message.answer(
        f"Тест завершен! Ваш результат: {correct_answers} из {total_questions} правильных ответов ({score:.2f}%).")
    await state.set_state(TestStates.SHOW_RESULT)


# Основная функция для запуска бота
async def main():
    # Начать опрос обновлений из Telegram
    await dp.start_polling(bot)


# Точка входа в сценарий
if __name__ == '__main__':
    # Настроить ведение журнала
    logging.basicConfig(level=logging.INFO)
    try:
        # Запустите основную функцию асинхронно
        asyncio.run(main())
    except KeyboardInterrupt:
        print('Выход')
