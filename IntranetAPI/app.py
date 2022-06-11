import hashlib
import os
import random
import time
import string

import asyncpg
import dotenv
from flask import Flask, request

dotenv.load_dotenv()
api = Flask(__name__)


@api.route('/api/v1/auth/register', methods=['POST'])
async def api_v1_auth_register():
    if request.headers.get("Content-Type") != "application/json; charset=utf-8":
        return {
            'code': 400,
            'status': 'Bad Request',
            'message': 'expected JSON body'
        }, 400

    conn = await asyncpg.connect(user=os.getenv("PGSQL_USERNAME"), password=os.getenv("PGSQL_PASSWORD"),
                                 database="postgres", host="127.0.0.1")

    try:
        body = request.json

        async with conn.transaction():
            now = time.time()
            salt = ''.join(random.choices(string.ascii_letters, k=10))

            hashed_pwd = hashlib.sha256(bytes(body['password'] + salt, 'utf-8')).hexdigest()

            await conn.execute(
                f"INSERT INTO users (username, pwd_saltedhash, user_id, salt) VALUES ('{body['username']}', '{hashed_pwd}', '{now}', '{salt}') "
            )
    except KeyError:
        return {
            'code': 422,
            'status': 'Un-processable Entity',
            'message': 'invalid request body'
        }, 422

    return {
        'code': 200,
        'status': 'OK',
        'message': 'the action completed successfully'
    }, 200


@api.route('/api/v1/auth/login', methods=['POST'])
async def api_v1_auth_login():
    if request.headers.get("Content-Type") != "application/json":
        return {
            'code': 400,
            'status': 'Bad Request',
            'message': 'expected JSON body'
        }, 400

    conn = await asyncpg.connect(user=os.getenv("PGSQL_USERNAME"), password=os.getenv("PGSQL_PASSWORD"))

    try:
        body = request.json

        async with conn.transaction():
            cur = conn.cursor(f'SELECT pwd_saltedhash, salt FROM users WHERE username = {body["username"]}')

            print(await cur.fetchrow())
    except KeyError:
        return {
            'code': 422,
            'status': 'Un-processable Entity',
            'message': 'invalid request body'
        }, 422

    return {
        'code': 200,
        'status': 'OK',
        'message': 'the action completed successfully'
    }, 200
