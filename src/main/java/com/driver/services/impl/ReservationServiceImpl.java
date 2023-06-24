package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {
     Optional<ParkingLot> parkingLotOptional = parkingLotRepository3.findById(parkingLotId);
      if(! parkingLotOptional.isPresent()) return null;

      Optional<User> userOptional = userRepository3.findById(userId);
      if(! userOptional.isPresent()) return null;

      List<Spot> spotList = parkingLotOptional.get().getSpotList();

      Reservation reservation = new Reservation();

      Spot optimalSpot = null;
      int optimalPrice = Integer.MAX_VALUE;

      for(Spot spot : spotList)
      {
          if(spot.getOccupied() == false) {
              if (spot.getSpotType().equals(SpotType.TWO_WHEELER)) {
                  if (numberOfWheels <= 2) {
                      if(optimalPrice > spot.getPricePerHour())
                      {
                          optimalPrice = spot.getPricePerHour();
                          optimalSpot = spot;
                      }
                  }
              }
              else if (spot.getSpotType().equals(SpotType.FOUR_WHEELER)) {
                  if (numberOfWheels <= 4) {
                      if(optimalPrice > spot.getPricePerHour())
                      {
                          optimalPrice = spot.getPricePerHour();
                          optimalSpot = spot;
                      }
                  }
              }
              else {
                  if(optimalPrice > spot.getPricePerHour())
                  {
                      optimalPrice = spot.getPricePerHour();
                      optimalSpot = spot;
                  }
              }
          }
      }
      if(optimalSpot == null) throw new Exception("Cannot make reservation");

      User user = userOptional.get();

      optimalSpot.setOccupied(false);
      reservation.setSpot(optimalSpot);
      reservation.setNumberOfHours(timeInHours);
      reservation.setUser(user);

      reservation = reservationRepository3.save(reservation);

      user.getReservationList().add(reservation);
      optimalSpot.getReservationList().add(reservation);

      userRepository3.save(user);
      spotRepository3.save(optimalSpot);

      return reservation;
    }
}
