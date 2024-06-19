import asyncio
import logging
import os
from aiohttp import ClientSession
from dotenv import load_dotenv
from aiogram import Bot, Dispatcher
from aiogram.types import Message, ReplyKeyboardMarkup, KeyboardButton, InlineKeyboardMarkup, InlineKeyboardButton, ReplyKeyboardRemove
from aiogram.filters import CommandStart, Command
from aiogram.types import CallbackQuery

# Загрузить переменные среды из файла .env
load_dotenv()

# Инициализируйте бота с помощью токена из переменных среды.
bot = Bot(token=os.getenv('TOKEN'))

# Инициализируйте диспетчер.
dp = Dispatcher()

# Определить кнопки для главного меню бота
button_tests = KeyboardButton(text='Тесты')
button_rating = KeyboardButton(text='Рейтинг')
button_results = KeyboardButton(text='Мои результаты')
button_help = KeyboardButton(text='Помощь')

# Создайте экземпляр ReplyKeyboardMarkup для главного меню.
main_keyboard = ReplyKeyboardMarkup(
    keyboard=[
        [button_tests],
        [button_results],
        [button_rating],
        [button_help]
    ],
    resize_keyboard=True
)

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
async def handle_tests_button(message: Message):
    async with ClientSession() as session:
        try:
            async with session.get('http://localhost:3333/admin/get/test') as response:
                if response.status == 200:
                    tests = await response.json()
                    logging.info(f"Полученные данные: {tests}")

                    inline_keyboard = InlineKeyboardMarkup(row_width=2)
                    for test in tests:
                        test_button = InlineKeyboardButton(text=test['title'], callback_data=f"test_{test['testId']}")
                        inline_keyboard.add(test_button)

                    # Отправьте сообщение с инлайн кнопками и удалите реплай кнопки
                    await message.answer("Выберите тест:", reply_markup=inline_keyboard)
                    await message.answer("Реплай кнопки скрыты.", reply_markup=ReplyKeyboardRemove())
                else:
                    await message.answer("Произошла ошибка при загрузке списка тестов")
        except Exception as e:
            logging.error(f"Ошибка при выполнении запроса на бэкенд: {e}")
            await message.answer("Произошла ошибка при выполнении запроса на сервер")

# Обработчик для инлайн-кнопок
@dp.callback_query()
async def handle_test_selection(callback_query: CallbackQuery):
    test_id = callback_query.data.split("_")[1]
    async with ClientSession() as session:
        try:
            # Делаем запрос на сервер для получения информации о выбранном тесте
            async with session.get(f'http://localhost:3333/admin/get/test/{test_id}') as response:
                if response.status == 200:
                    test_info = await response.json()
                    test_title = test_info.get('title', 'Название теста')
                    test_description = test_info.get('description', 'Описание теста')

                    # Отправляем информацию о тесте пользователю
                    await callback_query.message.answer(f"Тест: **{test_title}**\nОписание: **{test_description}**")
                else:
                    await callback_query.message.answer("Произошла ошибка при загрузке информации о тесте")
        except Exception as e:
            logging.error(f"Ошибка при выполнении запроса на бэкенд: {e}")
            await callback_query.message.answer("Произошла ошибка при выполнении запроса на сервер")

    # Ответ на callback_query, чтобы убрать часы ожидания
    await callback_query.answer()

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
