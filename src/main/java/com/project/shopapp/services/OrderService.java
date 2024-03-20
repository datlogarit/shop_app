package com.project.shopapp.services;

import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.OrderStatus;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.responses.UserResponseForOrder;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService{
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    //private final UserResponseForOrder userResponseForOrder;
    private final ModelMapper modelMapper;
    @Override
    public Order createOrder(OrderDTO orderDTO) throws Exception {
        //check if userID exist ?
        User user = userRepository
                .findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find user with id: "+orderDTO.getUserId()));
        //use lib Model Mapper to convert orderDTO => Order
        //from this modelMapper, I have the copies object have the same type
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));

        //update order from orderDTO
        Order order = new Order();
        modelMapper.map(orderDTO, order);
        order.setUser(user);//userResponseForOrder.fromUser(user)
        order.setOrderDate(new Date());//get the current time
        order.setStatus(OrderStatus.PENDING);
        //check shipping date has to >= today
        LocalDate shippingDate = orderDTO.getShippingDate() == null
                ? LocalDate.now() : orderDTO.getShippingDate();
        if (shippingDate.isBefore(LocalDate.now())) {
            throw new DataNotFoundException("Date must be at least today !");
        }
        order.setShippingDate(shippingDate);
        order.setActive(true);
        orderRepository.save(order);
        return order;
    }

    @Override
    public Order getOrder(Long id) {
        return orderRepository.findById(id).orElseThrow(()->new RuntimeException("Order not found"));
    }

    @Override
    public Order updateOrder(Long id, OrderDTO orderDTO)
            throws DataNotFoundException {
        Order existOrder = getOrder(id);
        User existUser= userRepository.findById(orderDTO.getUserId()).orElseThrow(()->new RuntimeException("user id not found"));
        existOrder.setUser(existUser);
        existOrder.setFullName(orderDTO.getFullName());
        existOrder.setEmail(orderDTO.getEmail());
        existOrder.setPhoneNumber(orderDTO.getPhoneNumber());
        existOrder.setAddress(orderDTO.getAddress());
        existOrder.setNote(orderDTO.getNote());
        existOrder.setTotalMoney(orderDTO.getTotalMoney());
        existOrder.setShippingAddress(orderDTO.getShippingAddress());
        existOrder.setPaymentMethod(orderDTO.getPaymentMethod());
        return orderRepository.save(existOrder);
    }

    @Override
    public void deleteOrder(Long id) {
        Order existOrder = orderRepository.findById(id).orElseThrow(null);
        if (existOrder!=null){
            existOrder.setActive(false);
            orderRepository.save(existOrder);
        }

    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }
}
