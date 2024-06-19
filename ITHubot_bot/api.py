import asyncpg
import os
from fastapi import FastAPI
from dotenv import load_dotenv
from pydantic import BaseModel

load_dotenv()

DB_HOST = os.getenv('DB_HOST')
DB_NAME = os.getenv('DB_NAME')
DB_USER = os.getenv('DB_USER')
DB_PASS = os.getenv('DB_PASS')

app = FastAPI()


class Test(BaseModel):
    id: int
    name: str
    callback_data: str


@app.on_event("startup")
async def startup():
    app.state.pool = await asyncpg.create_pool(
        host=DB_HOST,
        database=DB_NAME,
        user=DB_USER,
        password=DB_PASS
    )


@app.on_event("shutdown")
async def shutdown():
    await app.state.pool.close()


