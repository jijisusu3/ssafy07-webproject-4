# from typing import Union
# from pydantic import BaseModel, Field
# from app.schemes.common import CommonResponse

# class StockList(BaseModel):
#   id: int
#   fluctuation_rate: Union[float, None] = Field(description="등락률")
#   price: Union[int, None] = Field(description="현재가")
#   name: Union[str, None] = Field(description="종목이름")
#   ticker: Union[str, None]


# class Favorite(StockList):
#   user_id: int
#   stock


# class GetFavoriteStockList(Favorite, CommonResponse):
#   pass

# class GetTopStockList(StockList, CommonResponse):
#   pass