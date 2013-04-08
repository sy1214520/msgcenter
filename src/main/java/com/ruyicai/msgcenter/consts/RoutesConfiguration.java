package com.ruyicai.msgcenter.consts;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RoutesConfiguration {
	
	private Logger logger = LoggerFactory.getLogger(RoutesConfiguration.class);

	@Resource(name = "lotteryCamelContext")
	private CamelContext lotteryCamelContext;
	
	@Resource(name = "secondCamelContext")
	private CamelContext secondCamelContext;
	
	@PostConstruct
	public void init() throws Exception{
		logger.info("init camel routes");
		lotteryCamelContext.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("jms:queue:VirtualTopicConsumers.msgcenter.drawLottery").to("bean:sendOpenPrizeListener?method=openPrizeCustomer").routeId("消费开奖短信");
				//from("jms:queue:VirtualTopicConsumers.msgcenter.sendOpenSMS2User").to("bean:sendOpenPrizeListener?method=sendOpenSMS2User").routeId("发送开奖短信");
				from("jms:queue:VirtualTopicConsumers.msgcenter.orderPirzeend?concurrentConsumers=20").to("bean:sendEncashSMSListener?method=encashCustomer").routeId("中奖短信");
				from("jms:queue:VirtualTopicConsumers.msgcenter.sendAddNumSMS").to("bean:sendAddNumSMSListener?method=addNumCustomer").routeId("追号金额不足短信");
				from("jms:queue:VirtualTopicConsumers.msgcenter.sendAddNumEndSMS").to("bean:sendAddNumEndSMSListener?method=addNumEndCustomer").routeId("追号提醒短信");
				from("jms:queue:VirtualTopicConsumers.msgcenter.sendsms").to("bean:sendSMSListener?method=sendSMS").routeId("发送短信");
				//from("jms:queue:VirtualTopicConsumers.msgcenter.orderbetTopic").to("bean:sendSaveOrderSMSListener?method=saveOrderSMSCustomer").routeId("订单保存成功短信");
				//from("jms:queue:VirtualTopicConsumers.msgcenter.receiveSMSMessage").to("bean:receiveSMSListener?method=receiveSMSCustomer").routeId("接收上行短信");
				//from("jms:queue:VirtualTopicConsumers.msgcenter.send91MS2UserProducer").to("bean:sendSMSListener?method=sendIOSMS").routeId("发送IOS消息");
				from("jms:queue:VirtualTopicConsumers.msgcenter.savePresent").to("bean:sendSavePresentSMSListener?method=sendSavePresentSMSCustomer").routeId("发送赠送短信");
			}
		});
		
		secondCamelContext.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				deadLetterChannel("jms:queue:dead").maximumRedeliveries(-1)
				.redeliveryDelay(3000);
				from("secondjms:queue:VirtualTopicConsumers.msgcenter.sendOpenSMS2User?concurrentConsumers=20").to("bean:sendOpenPrizeListener?method=sendOpenSMS2User").routeId("发送开奖短信");
				from("secondjms:queue:VirtualTopicConsumers.msgcenter.sendIOS2UserProducer").to("bean:sendIOSListener?method=sendIOSMS").routeId("发送IOS消息");
				from("secondjms:queue:VirtualTopicConsumers.msgcenter.receiveSMSMessage").to("bean:receiveSMSListener?method=receiveSMSCustomer").routeId("接收上行短信");
			}
		});
	}
}
