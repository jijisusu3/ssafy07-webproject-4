import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import axios from "axios";

const api2 = "https://k7b204.p.ssafy.io/api2/";

const initialState = {
  user: {
    isLoggin: false,
    token: "",
    quizDay: "",
    likeList: [],
    haveList: [],
    data: {},
  },
  quizData: {},
};

const nicknamePut = createAsyncThunk("user/edit-nickname", async (data) => {
  // console.log(data.editname)
  // console.log(data.config)
  return axios
    .put(`${api2}users/nickname`, data.editname, data.config)
    .then(() => {
      return data.editname.nickname;
    });
});

const likeStockPost = createAsyncThunk(
  "stock-detail/likeStockPost",
  async (data) => {
    const config = {
      headers: {
        Authorization: `Bearer ${data.token}`,
      },
    };
    const stockId = {
      stockId: data.id,
    };
    return axios
      .post(`${api2}users/like`, stockId, config)
      .then((response) => {
        return response.data.favoriteStockCodeList;
      })
      .catch((err) => {
        console.log(err.data);
      });
  }
);

const quizGet = createAsyncThunk("stockList/quizGet", async (data) => {
  const config = {
    headers: {
      Authorization: `Bearer ${data}`,
    },
  };
  return axios.get(`${api2}quiz`, config).then((response) => {
    return response.data;
  });
});

const quizPut = createAsyncThunk("stockList/quizResult", async (data) => {
  const config = data.config;
  const quizResult = data.quizResult;
  return axios
    .put(`${api2}quiz`, quizResult, config)
    .then((response) => {
      console.log(response.data.message);
      return response.data.message;
    })
    .catch((err) => {
      console.log(err.data);
    });
});

export const userSlice = createSlice({
  name: "userSlice",
  initialState: initialState,
  reducers: {
    setLogin: (state, action) => {
      // console.log(action.payload)
      state.user.isLoggin = true;
      state.user.token = action.payload.token;
      state.user.likeList = action.payload.user.favoriteStockCode;
      state.user.haveList = action.payload.user.stockInfo;
      state.user.quizDay = action.payload.user.quizDay;
      localStorage.setItem("login-token", state.user.token);
      state.user.data = action.payload.user;
    },
    setLogout: () => {
      window.localStorage.clear();
      window.location.replace("/login");
    },
  },
  extraReducers: (builder) => {
    builder.addCase(likeStockPost.fulfilled, (state, action) => {
      state.user.likeList = action.payload;
    });
    builder.addCase(quizPut.fulfilled, (state, action) => {
      let date = new Date();
      const now = `${date.getFullYear()}-${("00" + (date.getMonth() + 1))
        .toString()
        .slice(-2)}-${("00" + date.getDate()).toString().slice(-2)}`;
      state.user.quizDay = now;
    });
    builder.addCase(nicknamePut.fulfilled, (state, action) => {
      state.user.data.nickname = action.payload;
    });
    builder.addCase(quizGet.fulfilled, (state, action) => {
      state.quizData = action.payload;
    });
  },
});

export const { setLogin, setLogout } = userSlice.actions;
export { likeStockPost, quizPut, nicknamePut,  quizGet };
