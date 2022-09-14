package cn.iocoder.yudao.module.market.convert.price;

import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.module.market.api.price.dto.PriceCalculateReqDTO;
import cn.iocoder.yudao.module.market.api.price.dto.PriceCalculateRespDTO;
import cn.iocoder.yudao.module.product.api.sku.dto.ProductSkuRespDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper
public interface PriceConvert {

    PriceConvert INSTANCE = Mappers.getMapper(PriceConvert.class);

    default PriceCalculateRespDTO convert(PriceCalculateReqDTO calculateReqDTO, List<ProductSkuRespDTO> skuList) {
        // 创建 PriceCalculateRespDTO 对象
        PriceCalculateRespDTO priceCalculate = new PriceCalculateRespDTO();
        priceCalculate.setOrder(new PriceCalculateRespDTO.Order().setSkuOriginalPrice(0).setSkuPromotionPrice(0)
                .setOrderPromotionPrice(0).setDeliveryPrice(0).setPayPrice(0).setItems(new ArrayList<>())
                .setCouponId(calculateReqDTO.getCouponId()));
        priceCalculate.setPromotions(new ArrayList<>());
        // 创建它的 OrderItem 属性
        Map<Long, Integer> skuIdCountMap = CollectionUtils.convertMap(calculateReqDTO.getItems(),
                PriceCalculateReqDTO.Item::getSkuId, PriceCalculateReqDTO.Item::getCount);
        skuList.forEach(sku -> {
            Integer count = skuIdCountMap.get(sku.getId());
            PriceCalculateRespDTO.OrderItem orderItem = new PriceCalculateRespDTO.OrderItem().setCount(count)
                    .setOriginalPrice(sku.getPrice()).setTotalOriginalPrice(sku.getPrice() * count).setTotalPromotionPrice(0);
            orderItem.setTotalPresentPrice(orderItem.getTotalPresentPrice()).setPresentPrice(orderItem.getOriginalPrice())
                    .setTotalPayPrice(orderItem.getTotalPayPrice());
            priceCalculate.getOrder().getItems().add(orderItem);
        });
        return priceCalculate;
    }

}