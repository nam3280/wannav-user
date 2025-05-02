package com.ssg.wannavapibackend.controller.web;

import com.ssg.wannavapibackend.dto.response.EventCouponResponseDTO;
import com.ssg.wannavapibackend.dto.response.EventListResponseDTO;
import com.ssg.wannavapibackend.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/promotion")
public class EventController {

    private final EventService eventService;

    @GetMapping("/events")
    public String eventList(Model model) {
        List<EventListResponseDTO> eventList = eventService.getEventList();
        model.addAttribute("eventList", eventList);
        return "promotion/events";
    }

    @GetMapping("/{eventId}")
    public String event(@PathVariable Long eventId, Model model) {
        EventCouponResponseDTO eventCouponResponseDTO = eventService.getEvent(eventId);
        model.addAttribute("eventCouponResponseDTO", eventCouponResponseDTO);
        return "promotion/event";
    }
}
