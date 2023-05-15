package dacs.nguyenhuubang.bookingwebsiteV1.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchTrip {
    private Boolean is_round_trip=false;
    private Integer s_startCity;
    private Integer s_endCity;
    private LocalDateTime s_endTime;
    private LocalDateTime s_startTime;
}
