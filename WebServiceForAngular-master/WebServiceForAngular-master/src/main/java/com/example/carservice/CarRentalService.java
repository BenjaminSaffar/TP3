package com.example.carservice;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.QueueConnectionFactory;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

@RestController
public class CarRentalService {
	
	private List<Car> cars = new ArrayList<Car>();
	
	public CarRentalService() {
		cars.add(new Car("11AA22", "Ferrari", 1000));
		cars.add(new Car("33BB44", "Porshe", 2222));
	}

	@CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping(value="/cars", method=RequestMethod.GET) 
	@ResponseStatus(HttpStatus.OK) 
	public List<Car> getListOfCars(){
		return cars;
	}

	@CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping(value="/cars/{plateNumber}", method=RequestMethod.GET) 
	@ResponseStatus(HttpStatus.OK) 
	public Car getListOfCars(@PathVariable(value="plateNumber") String plateNumber){
		for(Car car: cars) {
			if(car.getPlateNumber().equals(plateNumber)) {
				return car;
			}
		}
		return null;
	}

	@CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping(value = "/cars", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public Car addCar(@RequestBody Car car) throws Exception{
		cars.add(car);
		
			try{
			
			ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContextJMS.xml");
			TopicConnectionFactory factoryTopic = (TopicConnectionFactory) applicationContext.getBean("connectionFactory");
			Topic topic = (Topic) applicationContext.getBean("topic");
			TopicConnection connection = factoryTopic.createTopicConnection();
			TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			TopicPublisher sender = session.createPublisher(topic);
			ObjectMessage aCar = session.createObjectMessage(car);
			sender.send(aCar);
			session.close();
			connection.close();
			
			} catch(Exception e) {
				e.printStackTrace();
			}
		return car;
	}

}
