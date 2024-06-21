import asyncio
import logging
import os

import aiohttp
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


async def delete_message(chat_id, message_id):
    try:
        await bot.delete_message(chat_id=chat_id, message_id=message_id)
    except Exception as e:
        logging.error(f"Ошибка при удалении сообщения: {e}")


async def handle_exit_test(callback_query: CallbackQuery, state: FSMContext):
    data = await state.get_data()
    # Delete the tests page message
    prev_message_id = data.get('message_id')
    if prev_message_id:
        await delete_message(callback_query.message.chat.id, prev_message_id)
    # Call the /start command handler
    await cmd_start(callback_query.message)


# Define handler for the /start command.
@dp.message(CommandStart())
async def cmd_start(message: Message, state: FSMContext):
    userid = message.from_user.id
    async with aiohttp.ClientSession() as session:
        # Попытка входа в систему
        signin_request = {"username": userid, "password":userid+0000}
        async with session.post('http://localhost:3333/main/signin', json=signin_request) as response:
            if response.status == 200:
                await bot.send_message(message.chat.id, "Вы успешно вошли в систему!")
            else:
                # Попытка регистрации
                signup_request = {"username": userid, "password": userid+0000}
                async with session.post('http://localhost:3333/main/signup', json=signup_request) as response:
                    if response.status == 200:
                        await bot.send_message(message.chat.id, "Вы успешно зарегистрировались!")
                    else:
                        await bot.send_message(message.chat.id, "Ошибка при регистрации или входе в систему.")

    await bot.send_message(
        message.chat.id,
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
                    test_buttons = [[InlineKeyboardButton(text=test['title'], callback_data=f"test_{test['testId']}")]
                                    for test in tests_page]

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
                        await delete_message(message.chat.id, prev_message_id)

                    # Send the new page message and save the message_id
                    sent_message = await message.answer(f"Список доступных тестов (страница {page + 1}):",
                                                        reply_markup=inline_kb)
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


@dp.callback_query(lambda callback_query: callback_query.data.startswith("answer_"))
async def handle_answer_selection(callback_query: CallbackQuery, state: FSMContext):
    # Extract the answer_id from the callback data
    answer_id = callback_query.data.split("_")[1]

    # Get the current state data
    data = await state.get_data()

    # Get the current question
    current_question = data['current_question']
    questions = data['questions']
    question = questions[current_question]

    # Make a request to the server to get the answers for the current question
    async with ClientSession() as session:
        async with session.get(f'http://localhost:3333/admin/get/question/answer/{question["questionId"]}') as response:
            if response.status == 200:
                answers = await response.json()
            else:
                logging.error(f"Error fetching answers: {response.status}")
                await callback_query.message.answer("Произошла ошибка при загрузке ответов")
                return

    # Find the selected answer
    selected_answer = next((answer for answer in answers if str(answer['answerId']) == answer_id), None)

    if selected_answer:
        # If the answer is found, add it to the user's answers
        user_answers = data.get('user_answers', [])
        user_answers.append(selected_answer)
        await state.update_data(user_answers=user_answers)

        # Move to the next question or finish the test if this was the last question
        if current_question + 1 < len(questions):
            await state.update_data(current_question=current_question + 1)
            await ask_question(callback_query.message, state)
        else:
            await show_result(callback_query.message, state)
    else:
        # If the answer is not found, send an error message
        await callback_query.message.answer("Произошла ошибка при выборе ответа.")


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
                                    await delete_message(message.chat.id, prev_message_id)

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


# Function to display test results and save them to the server
async def show_result(message: Message, state: FSMContext):
    data = await state.get_data()
    user_answers = data['user_answers']
    questions = data['questions']
    test_id = data['test_id']
    user_id = data['user_id']  # Get the user id from the state
    score = sum(1 for answer in user_answers if answer['isCorrect'])  # Corrected key name to 'isCorrect'

    # Delete the last question message if exists
    prev_message_id = data.get('message_id')
    if prev_message_id:
        await delete_message(message.chat.id, prev_message_id)

    # Save the result to the server
    result_request = {
        "userId": user_id,
        "testId": test_id,
        "userAnswers": {question['questionId']: answer['answerId'] for question, answer in zip(questions, user_answers)}
    }
    async with ClientSession() as session:
        async with session.post('http://localhost:3333/admin/create/result', json=result_request) as response:
            if response.status == 200:
                await message.answer(f"Тест завершен! Ваш результат: {score} из {len(questions)} правильных ответов.",
                                     reply_markup=main_keyboard)
            else:
                logging.error(f"Error saving result: {response.status}")
                await message.answer("Произошла ошибка при сохранении результата.")

    await state.clear()
@dp.callback_query()
async def handle_all_other_updates(callback_query: CallbackQuery):
    logging.info(f"Unhandled update: {callback_query.data}")


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
