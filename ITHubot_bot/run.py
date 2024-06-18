import asyncio
import logging

from aiogram import Bot, Dispatcher
from aiogram.filters import CommandStart, Command
from aiogram.types import Message, ReplyKeyboardMarkup, KeyboardButton
from config import TOKEN

bot = Bot(token=TOKEN)
dp = Dispatcher()

# Создание кнопок
button_tests = KeyboardButton(text='Тесты')
button_rating = KeyboardButton(text='Рейтинг')
button_results = KeyboardButton(text='Мои результаты')
button_help = KeyboardButton(text='Помощь')

# Создание клавиатуры
main_keyboard = ReplyKeyboardMarkup(
    keyboard=[
        [button_tests],
        [button_rating],
        [button_results],
        [button_help]
    ],
    resize_keyboard=True  # Необязательный параметр
)

# Обработка команды /start с использованием клавиатуры
@dp.message(CommandStart())
async def cmd_start(message: Message):
    await message.answer(
        "Привет! Я бот для прохождения тестов. Вы можете выбрать тест из списка и начать его прохождение.\n"
        "\n"
        "Используйте кнопку Тесты для выбора теста.\n"
        "\n"
        "Для получения помощи используйте кнопку Помощь.",
        reply_markup=main_keyboard  # Отправка клавиатуры вместе с сообщением
    )

# Обработка команды /help
@dp.message(Command('help'))
async def get_help(message: Message):
    await message.answer(
        "Этот бот предназначен для прохождения тестов. Вот список доступных команд:\n"
        "/start - начать прохождение тестов или получить список команд\n"
        "/quiz - выбрать и начать прохождение теста\n"
        "/rating - посмотреть рейтинг участников\n"
        "/results - посмотреть свои результаты\n"
        "\n"
        "Если у вас возникли вопросы или проблемы, обратитесь к администратору."
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
