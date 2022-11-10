import datetime
import pytest
from httpx import AsyncClient

from app.models.stocks import Stock, Category
from app.const import *


@pytest.mark.anyio
class TestStockDetail:
    # When:
    @pytest.mark.anyio
    async def test_stock_exist_no_chart_data(self, client: AsyncClient):
        # 업종명: category_1,
        # 해당 업종으로 종목 TMPCORP 생성
        category_tmp = await Category.create(
            name='category_1',
            keyword=[1],
            info='category for test',
            sentiment=[20.83934247493744, 77.13800072669983, 2.022657170891762],
            represent=[]
        )
        stock_tmp = await Stock.create(
            ticker='999999',
            name='TMPCORP',
            price=5400,
            close_price=6000,
            fluctuation_rate=-10,
            fluctuation_price=-600,
            minimum=1,
            maximum=10,
            capital=123,
            trading_value=100,
            volume=1000,
            m_capital=54000000,
            issued=10000,
            category_id=category_tmp.pk,
            keyword=[],
            sentiment=[]
        )
        res = await client.get("/stocks/detail/999999")
        res_data = res.json()
        assert res.status_code == 200
        assert res_data["message"] == "success"
        assert res_data["ticker"] == stock_tmp.ticker
        assert res_data["name"] == stock_tmp.name
        assert res_data["price"] == stock_tmp.price
        assert res_data["daily"] == []
        assert res_data["weekly"] == []
        assert res_data["monthly"] == []
        assert res_data["yearly"] == []

    async def test_stock_exist_with_chart_data(self, client: AsyncClient):
        # 종목 TMPCORP에 대한 차트용 데이터
        stock = await Stock.get(ticker="999999")
        for k in range(37):
            await candle_map[stock.category_id].create(
                time=f'{(9+k//6)//10}{(9+k//6)%10}{k%6}000',
                price=1000+10*k,
                volume=100+k,
                open_price=1000,
                min_price=990,
                max_price=1000+10*k,
                stock_id=stock.pk,
                date=datetime.date.today()
            )
        for k in range(8):
            await day_map[stock.category_id].create(
                date=datetime.date.today()+datetime.timedelta(k-7),
                volume=100+k,
                open_price=1000+10*k,
                close_price=1100+10*k,
                min_price=980+10*k,
                max_price=1100+10*k,
                stock_id=stock.pk,
            )
        res = await client.get("/stocks/detail/999999")
        res_data = res.json()
        today = datetime.date.today()
        assert res.status_code == 200
        assert res_data["message"] == "success"
        assert res_data["daily"][0]["time"] == "090000"
        assert res_data["daily"][-1]["time"] == "150000"
        assert res_data["daily"][5]["date"] == today.strftime("%Y-%m-%d")
        assert res_data["weekly"][-1]["time"] == "150000"
        assert len(res_data['weekly']) == (5+37)//6
        assert res_data["monthly"][0]["date"] == (today-datetime.timedelta(7)).strftime("%Y-%m-%d")
        assert res_data["monthly"][7]["date"] == today.strftime("%Y-%m-%d")
        assert len(res_data["yearly"]) == 2

    @pytest.mark.anyio
    async def test_stock_does_not_exist(self, client: AsyncClient):
        # 없는 종목 번호
        res = await client.get("/stocks/detail/999988")
        res_data = res.json()
        assert res.status_code == 404
        assert res_data["message"] == "failed"


@pytest.mark.anyio
class TestShortStock:

    @pytest.mark.anyio
    async def test_stock_bad_request(self, client: AsyncClient):
        assert True

    @pytest.mark.anyio
    async def test_stock_does_not_exist(self, client: AsyncClient):
        res = await client.get("/stocks/short/999988")
        res_data = res.json()
        assert res.status_code == 404
        assert res_data["message"] == "failed"

    # @pytest.mark.anyio
    # async def test_stock_exist(self, client: AsyncClient):
    #     assert True
    #
    # @pytest.mark.anyio
    # async def test_stock_exist_but_some_null(self, client: AsyncClient):
    #     assert True

# @pytest.mark.anyio
# async def test_signup_success(client: AsyncClient):
#     # When: username, password로 회원가입을 성공했을 때
#     res = await client.post("/example/signup", json={"username": 'username', "password": 'password'})
#
#     # Then: 성공을 응답
#     res_data = res.json()
#     assert res_data["message"] == "success"
#
#
# @pytest.mark.anyio
# async def test_signup_failed(client: AsyncClient):
#     #Given
#     user, created = await User.get_or_create(username="username2", password="password")
#     print(user, created)
#     # When: username, password 등 이미 있는 아이디로 회원가입을 시도했을 때
#     res = await client.post("/example/signup", json={"username": 'username2', "password": 'password'})
#     print(res)
#     # Then: 실패 응답
#     res_data = res.json()
#     print(res_data)
#     assert res_data["detail"] == "실패"

#
# @pytest.mark.asyncio
# async def test_login_should_be_failed(async_client):
#     await User.create(username="username", password="password")
#     # When: 유저로 로그인을 했을 때
#     res = await async_client.post("/examples/login", json={"username": 'username', "password": 'password'})
#
#     # Then: 실패를 응답하고
#     res_data = res.json()
#     assert res_data["message"] == "success"
