package com.campusconnect.attendance.listener;

import com.campusconnect.attendance.config.RabbitMQConfig;
import com.campusconnect.attendance.event.IncomingEvent;
import com.campusconnect.attendance.service.AttendanceService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/** Consume StudentEnrolled para mantener la proyeccion local de estudiantes. */
@Component
public class StudentEventListener {

    private final AttendanceService service;

    public StudentEventListener(AttendanceService service) {
        this.service = service;
    }

    @RabbitListener(queues = RabbitMQConfig.Q_STUDENT)
    public void onStudentEnrolled(IncomingEvent event) {
        service.handleStudentEnrolled(event);
    }
}
