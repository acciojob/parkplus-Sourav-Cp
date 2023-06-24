package com.driver.services.impl;

import com.driver.model.Payment;
import com.driver.model.PaymentMode;
import com.driver.model.Reservation;
import com.driver.model.Spot;
import com.driver.repository.PaymentRepository;
import com.driver.repository.ReservationRepository;
import com.driver.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    ReservationRepository reservationRepository2;
    @Autowired
    PaymentRepository paymentRepository2;

    @Override
    public Payment pay(Integer reservationId, int amountSent, String mode) throws Exception {

        Reservation reservation = reservationRepository2.findById(reservationId).get();
        Spot spot = reservation.getSpot();

        int bill = spot.getPricePerHour()*reservation.getNumberOfHours();

        Payment payment = new Payment();
        payment.setReservation(reservation);

        String updateMode = mode.toUpperCase();
        payment.setPaymentCompleted(false);

        if(updateMode.equals("UPI"))
        {
            payment.setPaymentMode(PaymentMode.UPI);
        }
        else if(updateMode.equals("CASH"))
        {
            payment.setPaymentMode(PaymentMode.CASH);
        }
        else if(updateMode.equals("CARD"))
        {
            payment.setPaymentMode(PaymentMode.CARD);
        }
        else throw new Exception("Payment mode not detected");

        if(amountSent<bill)throw new Exception("Insufficient Amount");

        payment.setPaymentCompleted(true);
        spot.setOccupied(true);
        payment.setReservation(reservation);
        reservation.setPayment(payment);

        reservationRepository2.save(reservation);
        return payment;
    }
}
