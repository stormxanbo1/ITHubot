import asyncio
import logging
import os
from dotenv import load_dotenv

from aiogram import Bot, Dispatcher
from aiogram.filters import CommandStart, Command
from aiogram.types import Message, ReplyKeyboardMarkup, KeyboardButton

load_dotenv()
bot = Bot(token=os.getenv('TOKEN'))
dp = Dispatcher()

button_tests = KeyboardButton(text='Тесты')
button_rating = KeyboardButton(text='Рейтинг')
button_results = KeyboardButton(text='Мои результаты')
button_help = KeyboardButton(text='Помощь')

main_keyboard = ReplyKeyboardMarkup(
    keyboard=[
        [button_tests],
        [button_rating],
        [button_results],
        [button_help]
    ],
    resize_keyboard=True
)


@dp.message(CommandStart())
async def cmd_start(message: Message):
    await message.answer(
        "Привет! Я бот для прохождения тестов. Вы можете выбрать тест из списка и начать его прохождение.\n"
        "\n"
        "Для получения справки используйте кнопку 'Помощь'.",
        reply_markup=main_keyboard  # Отправка клавиатуры вместе с сообщением
    )

# Обработка команды /help
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

# Основная функция для запуска бота
async def main():
    await dp.start_polling(bot)

# Запуск бота
if __name__ == '__main__':
    logging.basicConfig(level=logging.INFO)
    try:
        asyncio.run(main())
    except KeyboardInterrupt:
        print('Exit')
