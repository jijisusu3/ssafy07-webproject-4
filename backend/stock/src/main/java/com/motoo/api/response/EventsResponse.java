package com.motoo.api.response;


import com.motoo.db.entity.Events;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventsResponse {
    private Long eventsId;

    private Date open_date;

    private Date close_date;

    private String hall_of_fame;

    public static EventsResponse response(Events events){
        return new EventsResponse(events.getEventsId(), events.getOpen_date(), events.getClose_date(), events.getHall_of_fame());
    }
}
