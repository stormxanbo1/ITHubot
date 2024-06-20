import asyncio
import logging
import os
from aiohttp import ClientSession
from dotenv import load_dotenv
from aiogram import Bot, Dispatcher
from aiogram.types import Message, ReplyKeyboardMarkup, KeyboardButton, InlineKeyboardMarkup, InlineKeyboardButton, \
    CallbackQuery, ReplyKeyboardRemove
from aiogram.filters import CommandStart, Command
from aiogram.fsm.context import FSMContext
from aiogram.fsm.storage.memory import MemoryStorage
from aiogram.fsm.state import State, StatesGroup

# Load environment variables from the .env file
load_dotenv()

# Initialize the bot with the token from environment variables.
bot = Bot(token=os.getenv('TOKEN'))

# Initialize dispatcher.
dp = Dispatcher(storage=MemoryStorage())

# Define buttons for the bot's main menu
button_tests = KeyboardButton(text='Тесты')
button_rating = KeyboardButton(text='Рейтинг')
button_results = KeyboardButton(text='Мои результаты')
button_help = KeyboardButton(text='Помощь')
button_exit = KeyboardButton(text='Выход')

# Create an instance of ReplyKeyboardMarkup for the main menu.
main_keyboard = ReplyKeyboardMarkup(
    keyboard=[
        [button_tests],
        [button_rating],
        [button_results],
        [button_help]
    ],
    resize_keyboard=True
)


exit_keyboard = ReplyKeyboardMarkup(
    keyboard=[
        [button_exit]
    ],
    resize_keyboard=True
)

class TestStates(StatesGroup):
    IN_TEST = State()
    IN_QUESTION = State()


@dp.callback_query(lambda callback_query: callback_query.data == 'exit_test')
async def handle_exit_test(callback_query: CallbackQuery, state: FSMContext):
    data = await state.get_data()
    # Delete the tests page message
    prev_message_id = data.get('message_id')
    if prev_message_id:
        try:
            await bot.delete_message(chat_id=callback_query.message.chat.id, message_id=prev_message_id)
        except Exception as e:
            logging.error(f"Ошибка при удалении сообщения: {e}")

    # Call the /start command handler
    await cmd_start(callback_query.message)


# Define handler for the /start command.
@dp.message(CommandStart())
async def cmd_start(message: Message):
    await message.answer(
        "Привет! Я бот для прохождения тестов. Вы можете выбрать тест из списка и начать его прохождение.\n"
        "\n"
        "Для получения справки используйте кнопку 'Помощь'.",
        reply_markup=main_keyboard  # Send the main keyboard with the message
    )


# Define handler for the /help command.
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


# Define handler for the "Тесты" button.
# Количество тестов на одной странице
TESTS_PER_PAGE = 5


# Обработчик для кнопки "Тесты"
@dp.message(lambda message: message.text == 'Тесты')
async def handle_tests_button(message: Message, state: FSMContext):
    page = 0  # Начинаем с первой страницы
    await state.update_data(tests_page=page)
    await show_tests_page(message, state, page)


# Функция для отображения страницы тестов
async def show_tests_page(message: Message, state: FSMContext, page: int):
    data = await state.get_data()  # Получаем данные из состояния FSM
    async with ClientSession() as session:
        try:
            async with session.get('http://localhost:3333/admin/get/test') as response:
                if response.status == 200:
                    tests = await response.json()
                    logging.info(f"Полученные данные: {tests}")  # Log the full JSON response

                    # Получаем тесты для текущей страницы
                    start = page * TESTS_PER_PAGE
                    end = start + TESTS_PER_PAGE
                    tests_page = tests[start:end]

                    # Создаем кнопки для тестов на текущей странице
                    test_buttons = [[InlineKeyboardButton(text=test['title'], callback_data=f"test_{test['testId']}")] for test in tests_page]

                    # Если это не первая страница, добавляем кнопку "Назад"
                    if page > 0:
                        test_buttons.append([InlineKeyboardButton(text="<< Назад", callback_data="prev_page")])

                    # Если это не последняя страница, добавляем кнопку "Вперед"
                    if end < len(tests):
                        test_buttons.append([InlineKeyboardButton(text="Вперед >>", callback_data="next_page")])

                    # Добавляем кнопку "Выход"
                    test_buttons.append([InlineKeyboardButton(text="Выход", callback_data="exit_test")])

                    inline_kb = InlineKeyboardMarkup(inline_keyboard=test_buttons)

                    # Delete the previous page message if exists
                    prev_message_id = data.get('message_id')
                    if prev_message_id:
                        try:
                            await bot.delete_message(chat_id=message.chat.id, message_id=prev_message_id)
                        except Exception as e:
                            logging.error(f"Ошибка при удалении сообщения: {e}")

                    # Send the new page message and save the message_id
                    sent_message = await message.answer(f"Список доступных тестов (страница {page + 1}):", reply_markup=inline_kb)
                    await state.update_data(message_id=sent_message.message_id)
                else:
                    await message.answer("Произошла ошибка при загрузке списка тестов")
        except Exception as e:
            logging.error(f"Ошибка при выполнении запроса на бэкенд: {e}")
            await message.answer("Произошла ошибка при выполнении запроса на сервер")
# Обработчики для кнопок "Вперед" и "Назад"
@dp.callback_query(lambda callback_query: callback_query.data in ["prev_page", "next_page"])
async def handle_page_buttons(callback_query: CallbackQuery, state: FSMContext):
    data = await state.get_data()
    page = data.get('tests_page', 0)

    if callback_query.data == "prev_page":
        page -= 1
    else:  # "next_page"
        page += 1

    await state.update_data(tests_page=page)
    await show_tests_page(callback_query.message, state, page)
    await callback_query.answer()


# Define handler for the "Помощь" button.
@dp.message(lambda message: message.text == 'Помощь')
async def handle_help_button(message: Message):
    await get_help(message)


# Handler for inline buttons
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

                        # Send the test selection message and clear the main keyboard
                        await callback_query.message.answer(
                            f"Вы выбрали тест:\n\nНазвание: {selected_test['title']}\nОписание: {selected_test['description']}\nНачнем тестирование!",
                            reply_markup=ReplyKeyboardRemove()  # Use ReplyKeyboardRemove to hide the keyboard
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


@dp.callback_query(lambda callback_query: callback_query.data == 'exit_test')
async def handle_exit_test(callback_query: CallbackQuery, state: FSMContext):
    data = await state.get_data()
    # Delete the tests page message
    prev_message_id = data.get('message_id')
    if prev_message_id:
        try:
            await bot.delete_message(chat_id=callback_query.message.chat.id, message_id=prev_message_id)
        except Exception as e:
            logging.error(f"Ошибка при удалении сообщения: {e}")

    # Call the /start command handler
    await cmd_start(callback_query.message)


# Function to request a question and display answer options
async def ask_question(message: Message, state: FSMContext):
    data = await state.get_data()
    test_id = data['test_id']
    current_question = data['current_question']
    async with ClientSession() as session:
        try:
            async with session.get(f'http://localhost:3333/admin/questions/{test_id}') as response:
                if response.status == 200:
                    questions = await response.json()
                    logging.info(f"Questions for test {test_id}: {questions}")

                    if current_question < len(questions):
                        question = questions[current_question]
                        await state.update_data(questions=questions)

                        async with session.get(
                                f'http://localhost:3333/admin/get/question/answer/{question["questionId"]}') as ans_response:
                            if ans_response.status == 200:
                                answers = await ans_response.json()
                                logging.info(f"Answers for question {question['questionId']}: {answers}")
                                buttons = [InlineKeyboardButton(text=answer['content'],
                                                                callback_data=f"answer_{answer['answerId']}") for answer
                                           in answers]
                                buttons.append(InlineKeyboardButton(text="Выход", callback_data="exit_test"))
                                inline_kb = InlineKeyboardMarkup(inline_keyboard=[buttons])

                                # Delete the previous question message if exists
                                prev_message_id = data.get('message_id')
                                if prev_message_id:
                                    try:
                                        await bot.delete_message(chat_id=message.chat.id, message_id=prev_message_id)
                                    except Exception as e:
                                        logging.error(f"Ошибка при удалении сообщения: {e}")

                                # Send the question and save the message_id
                                question_message = await message.answer(
                                    f"Вопрос {current_question + 1}: {question['content']}",
                                    reply_markup=inline_kb)
                                await state.update_data(message_id=question_message.message_id)

                            else:
                                logging.error(f"Error fetching answers: {ans_response.status}")
                                await message.answer("Произошла ошибка при загрузке ответов")
                    else:
                        await show_result(message, state)
                else:
                    logging.error(f"Error fetching questions: {response.status}")
                    await message.answer("Произошла ошибка при загрузке вопросов теста")
        except Exception as e:
            logging.error(f"Ошибка при выполнении запроса на бэкенд: {e}")
            await message.answer("Произошла ошибка при выполнении запроса на сервер")


# Function to display test results
async def show_result(message: Message, state: FSMContext):
    data = await state.get_data()
    answers = data['answers']
    score = sum(1 for answer in answers if answer['isCorrect'])  # Corrected key name to 'isCorrect'

    # Delete the last question message if exists
    prev_message_id = data.get('message_id')
    if prev_message_id:
        try:
            await bot.delete_message(chat_id=message.chat.id, message_id=prev_message_id)
        except Exception as e:
            logging.error(f"Ошибка при удалении сообщения: {e}")

    await message.answer(f"Тест завершен! Ваш результат: {score} из {len(answers)} правильных ответов.",
                         reply_markup=main_keyboard)
    await state.clear()


# Handler for answer button presses, including exit
@dp.callback_query(
    lambda callback_query: callback_query.data.startswith("answer_") or callback_query.data == 'exit_test')
async def handle_answer(callback_query: CallbackQuery, state: FSMContext):
    if callback_query.data == 'exit_test':
        await callback_query.message.answer("Вы вышли из теста.")
        await state.clear()
    else:
        answer_id = callback_query.data.split("_")[1]
        data = await state.get_data()
        current_question = data['current_question']
        questions = data['questions']
        selected_test = data['selected_test']

        async with ClientSession() as session:
            try:
                async with session.get(
                        f'http://localhost:3333/admin/get/question/answer/{questions[current_question]["questionId"]}') as ans_response:
                    if ans_response.status == 200:
                        answers = await ans_response.json()
                        answer = next((ans for ans in answers if str(ans['answerId']) == answer_id), None)

                        if answer:
                            data['answers'].append(answer)
                            await state.update_data(answers=data['answers'])

                            next_question = current_question + 1
                            await state.update_data(current_question=next_question)

                            if next_question < len(questions):
                                # Delete the previous question message if exists
                                prev_message_id = data.get('message_id')
                                if prev_message_id:
                                    await bot.delete_message(chat_id=callback_query.message.chat.id,
                                                             message_id=prev_message_id)

                                await ask_question(callback_query.message, state)
                            else:
                                await show_result(callback_query.message, state)
                        else:
                            await callback_query.message.answer("Произошла ошибка при обработке ответа")
                    else:
                        await callback_query.message.answer("Произошла ошибка при загрузке ответов")
                        logging.error(f"Error response status for answers: {ans_response.status}")
            except Exception as e:
                logging.error(f"Ошибка при выполнении запроса на бэкенд: {e}")
                await callback_query.message.answer("Произошла ошибка при выполнении запроса на сервер")
    await callback_query.answer()


async def main():
    # Start polling updates from Telegram
    await dp.start_polling(bot)


# Entry point for the script
if __name__ == '__main__':
    # Configure logging
    logging.basicConfig(level=logging.INFO)
    try:
        # Run the main function asynchronously
        asyncio.run(main())
    except KeyboardInterrupt:
        print('Выход')
