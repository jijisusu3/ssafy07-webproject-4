package com.motoo.api.controller;


import com.motoo.api.request.MakeAccountPostReq;
import com.motoo.api.request.MakeOrderPostReq;
import com.motoo.api.request.UpdateAccountNameReq;
import com.motoo.api.request.UpdateOrderReq;
import com.motoo.api.response.AccountListRes;
import com.motoo.api.response.AccountsListRes;
import com.motoo.api.response.TradingListRes;
import com.motoo.api.service.TradingService;
import com.motoo.api.service.UserService;
import com.motoo.common.model.response.BaseResponseBody;
import com.motoo.db.entity.Account;
import com.motoo.db.entity.Trading;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

@Api(value = "트레이딩 API", tags = {"Trading"})
@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api2/trading")
public class TradingController {


    private final TradingService tradingService;

    private final UserService userService;


    //주문 하기
    @ApiOperation(value = "주문 생성", notes = "(token) 주문을 생성한다.")
    @ApiResponses({@ApiResponse(code = 200, message = " 생성 성공", response = BaseResponseBody.class), @ApiResponse(code = 401, message = "주문 생성 실패", response = BaseResponseBody.class), @ApiResponse(code = 500, message = "서버 오류", response = BaseResponseBody.class)})
    @PostMapping()
    public ResponseEntity<? extends BaseResponseBody> createOrder(@ApiIgnore Authentication authentication, @RequestBody @ApiParam(value = "주문 상세 내용", required = true) @Valid MakeOrderPostReq makeOrderPostReq) throws Exception {
        Long userId =  userService.getUserIdByToken(authentication);
        try
        {
            tradingService.writeOrder(userId, makeOrderPostReq.getAccountId(),
                    makeOrderPostReq.getStockId(),
                    makeOrderPostReq.getTr_type(),
                    makeOrderPostReq.getPrice(),
                    makeOrderPostReq.getAmount());
        } catch(
                Exception e)
        {
            return ResponseEntity.status(401).body(BaseResponseBody.of(401, "주문 생성에 실패하였습니다."));
        }
        return ResponseEntity.status(200).

                body(BaseResponseBody.of(200, "주문이 생성되었습니다."));
    }
    //주문 리스트 조회
    @GetMapping()
    @ApiResponses({@ApiResponse(code = 200, message = "(token) 주문 목록 조회 성공", response = AccountListRes.class), @ApiResponse(code = 401, message = "주문 목록 조회 실패", response = BaseResponseBody.class), @ApiResponse(code = 500, message = "서버 오류", response = BaseResponseBody.class)})
    @ApiOperation(value = "주문 목록 조회", notes = "주문 목록을 조회한다.")
    public ResponseEntity<TradingListRes> listTradings(@ApiIgnore Authentication authentication){
        Long userId =  userService.getUserIdByToken(authentication);
        List<Trading> tradings= tradingService.tradingList(userId);

        System.out.println("트레이딩 조회");

        return ResponseEntity.status(200).body(TradingListRes.of(tradings, 200, "주문 목록조회에 성공하였습니다."));

    }

    //주문 삭제
    @DeleteMapping("/{tradeId}")
    @ApiResponses({@ApiResponse(code = 200, message = "(token) 주문 삭제 성공", response = BaseResponseBody.class), @ApiResponse(code = 401, message = "주문 삭제 실패", response = BaseResponseBody.class), @ApiResponse(code = 500, message = "서버 오류", response = BaseResponseBody.class)})
    @ApiOperation(value = "주문 삭제", notes = "주문을 삭제한다.")
    public ResponseEntity<? extends BaseResponseBody> deleteAccounts(@ApiIgnore Authentication authentication, @PathVariable @ApiParam(value = "상세번호", required = true) Long tradeId){
        Long userId =  userService.getUserIdByToken(authentication);

        Trading trading = tradingService.getTrading(userId, tradeId);
        if (trading  == null) return ResponseEntity.status(402).body(BaseResponseBody.of(402, "해당 주문이 없습니다."));
        int result = tradingService.deleteOrder( userId,tradeId);
        if (result == 1) return ResponseEntity.status(200).body(BaseResponseBody.of(200, "주문이 삭제되었습니다."));
        else return ResponseEntity.status(401).body(BaseResponseBody.of(401, "주문 삭제에 실패하였습니다."));
    }
    //주문수정
    @PutMapping("/{tradeId}")   //re
    @ApiResponses({@ApiResponse(code = 200, message = "(token) 주문 수정 성공", response = BaseResponseBody.class), @ApiResponse(code = 401, message = "주문 수정 실패", response = BaseResponseBody.class), @ApiResponse(code = 402, message = "해당 주문 없음", response = BaseResponseBody.class), @ApiResponse(code = 500, message = "서버 오류", response = BaseResponseBody.class)})
    @ApiOperation(value = "주문 수정", notes = "주문을 수정한다.")
    public ResponseEntity<? extends BaseResponseBody> updateTrading(@ApiIgnore Authentication authentication, @RequestBody @ApiParam(value = "주문 이름", required = true) @Valid UpdateOrderReq updateOrderReq, @PathVariable @ApiParam(value = "상세번호", required = true) Long tradeId){

        Long userId =  userService.getUserIdByToken(authentication);
        Trading trading = tradingService.getTrading(userId,tradeId);
        if (trading == null) return ResponseEntity.status(402).body(BaseResponseBody.of(402, "해당 주문이 없습니다."));
        try {
            tradingService.updateOrder(trading,updateOrderReq.getTr_price(), updateOrderReq.getTr_amount());
        } catch (Exception e) {
            return ResponseEntity.status(401).body(BaseResponseBody.of(401, "주문 수정에 실패했습니다."));
        }
        return ResponseEntity.status(200).body(BaseResponseBody.of(200, "주문 수정에 성공했습니다."));
    }


}