package com.example.uxn_common.global.domain.device.repository;

import com.example.uxn_common.global.domain.device.Device;
import com.example.uxn_common.global.domain.staff.Staff;
import com.example.uxn_common.global.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DeviceRepository extends JpaRepository<Device, String> {

//    DiabetesCheckDevice findByUser(User userId);

//    @Query("SELECT d FROM Device d ORDER BY d.createDataTime ASC ")
    List<Device> findByUser(User user);

    List<Device> findAllByUser(User user);

    Device findByDeviceId(String deviceId);

//    @Query("SELECT d FROM Device d ORDER BY d.createDataTime ASC")
//    Device findByIdx(Long id);

    List<Device> findByCreateDataTimeBetweenAndUser (LocalDateTime startDay, LocalDateTime endDay, User user);
    List<Device> findByCreateDataTimeEquals(LocalDateTime startDay);

    Device findTopByUserOrderByCreateDataTimeDesc(User user);

    //todo nativeQuery
    // @Query(value =  "SELECT item.createDataTime " +
    //     " FROM Device AS item " + 
    //     " WHERE item.user = :user group by DATE_FORMAT(item.createDataTime , '%Y-%m-%d') order by item.createDataTime desc"
    //     )
    @Query(value =  "SELECT DATE_FORMAT(item.createDataTime , '%Y-%m-%d') " +
        " FROM Device item " + 
        " WHERE item.user = :user GROUP BY DATE_FORMAT(item.createDataTime , '%Y-%m-%d') order by item.createDataTime desc")
    List<String> customGetDateList(User user);

    @Query(value =  "SELECT DATE_FORMAT(item.createDataTime , '%Y-%m-%d') " +
            " FROM Device item " +
            " WHERE item.user = :user and item.createDataTime between :start and :end GROUP BY DATE_FORMAT(item.createDataTime , '%Y-%m-%d')  order by item.createDataTime desc")
    List<String> customGetDateList(User user, LocalDateTime start, LocalDateTime end);

    void deleteAllByUser(User User);


}
