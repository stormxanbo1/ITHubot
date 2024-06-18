import asyncio
import logging

from aiogram import Bot, Dispatcher
from aiogram.filters import CommandStart, Command
from aiogram.types import Message, ReplyKeyboardMarkup
from config import TOKEN

bot = Bot(token=TOKEN)
dp = Dispatcher()


main = ReplyKeyboardMarkup(resize_keyboard=True)
main.add('Тесты').add('Рейтинг').add('Мои результаты').add('Помощь')


@dp.message(CommandStart())
async def cmd_start(message: Message):
    await message.answer(
        "Привет! Я бот для прохождения тестов. Вы можете выбрать тест из списка и начать его прохождение.\n"
        "\n"
        "Используйте команду /quiz для выбора теста.\n"
        "\n"
        "Для получения помощи используйте команду /help.",
        reply_markup=main
    )


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


async def main():
    await dp.start_polling(bot)


if __name__ == '__main__':
    logging.basicConfig(level=logging.INFO)
    try:
        asyncio.run(main())
    except KeyboardInterrupt:
        print('Exit')
