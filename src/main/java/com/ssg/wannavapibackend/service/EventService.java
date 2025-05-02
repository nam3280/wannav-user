package com.ssg.wannavapibackend.service;

import com.ssg.wannavapibackend.dto.request.EventCouponRequestDTO;
import com.ssg.wannavapibackend.dto.response.EventCouponResponseDTO;
import com.ssg.wannavapibackend.dto.response.EventListResponseDTO;

import java.util.List;

public interface EventService {
    List<EventListResponseDTO> getEventList();

    EventCouponResponseDTO getEvent(Long eventId);

    void couponDistribution(EventCouponRequestDTO eventCouponRequestDTO);
}