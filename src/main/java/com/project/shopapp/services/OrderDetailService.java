package com.project.shopapp.services;

import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.dtos.OrderDetailDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.OrderDetail;
import com.project.shopapp.models.Product;
import com.project.shopapp.repositories.OrderDetailRepository;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.responses.OrderDetailResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderDetailService implements IOrderDetailService{
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    @Override
    public OrderDetail createOrderDetail(OrderDetailDTO orderDetailDTO) throws Exception {
        //find out of orderId exists
        Order existOrder = orderRepository.findById(orderDetailDTO.getOrder()).orElseThrow(()
                ->new RuntimeException("Order not found"));
        // find product with id
        Product existProduct = productRepository.findById(orderDetailDTO.getProduct()).orElseThrow(()->
                new RuntimeException("product not found"));
        modelMapper.typeMap(OrderDetailDTO.class, OrderDetail.class)
                .addMappings(mapper -> mapper.skip(OrderDetail::setId));

        OrderDetail orderDetail = new OrderDetail();
        modelMapper.map(orderDetailDTO, orderDetail);
        orderDetail.setOrder(existOrder);
        orderDetail.setProduct(existProduct);
        //lưu vào db
        return orderDetailRepository.save(orderDetail);
    }

    @Override
    public OrderDetail getOrderDetail(Long id) throws DataNotFoundException {
        return orderDetailRepository.findById(id)
                .orElseThrow(()->new DataNotFoundException("OrderDetail not found"));
    }

    @Override
    public OrderDetail updateOrderDetail(Long id, OrderDetailDTO orderDetailDTO)
            throws DataNotFoundException {
        //find order detail to exist ?
        OrderDetail existOrderDetail = getOrderDetail(id);
        //get order for setOrder
        Order existOrder = orderRepository.findById(orderDetailDTO.getOrder()).orElseThrow(()
                ->new RuntimeException("Order not found"));
        //get product for setProduct
        Product existProduct = productRepository.findById(orderDetailDTO.getProduct()).orElseThrow(()->
                new RuntimeException("Product not found"));

        existOrderDetail.setOrder(existOrder);
        existOrderDetail.setProduct(existProduct);
        existOrderDetail.setPrice(orderDetailDTO.getPrice());
        existOrderDetail.setNumberOfProducts(orderDetailDTO.getNumberOfProducts());
        existOrderDetail.setTotalMoney(orderDetailDTO.getTotalMoney());
        existOrderDetail.setColor(orderDetailDTO.getColor());
        return orderDetailRepository.save(existOrderDetail) ;
    }

    @Override
    public void deleteById(Long id) {
        orderDetailRepository.deleteById(id);
    }

    @Override
    public List<OrderDetail> findByOrderId(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }
}
