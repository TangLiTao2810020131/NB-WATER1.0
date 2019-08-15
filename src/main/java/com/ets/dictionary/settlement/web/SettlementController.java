package com.ets.dictionary.settlement.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ets.system.log.opr.entity.tb_log_opr;
import com.ets.system.log.opr.service.LogOprService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ets.common.PageListData;
import com.ets.dictionary.settlement.entity.nb_settlement_dict;
import com.ets.dictionary.settlement.service.SettlementService;
import com.ets.utils.Message;
import com.google.gson.Gson;

/**
 * 结算方式数据字典
 * @author wh
 *
 */
@Controller
@RequestMapping("settlement")
public class SettlementController {

	@Resource
	SettlementService settlementService;
	@Resource
	LogOprService logService;

	String baseUrl = "dictionary/settlement/";
	
	String modulename = "数据字典-结算方式";

	@RequestMapping("list")
	public String list(HttpServletRequest request)
	{
		tb_log_opr log = new tb_log_opr();
		log.setModulename(modulename);
		log.setOprcontent("访问结算方式列表");
		logService.addLog(log);
		return baseUrl+"settlement-list";
	}

	@RequestMapping("input")
	public String input(HttpServletRequest request,String id)
	{
		nb_settlement_dict settlement = null;
		if(id!=null && !id.equals(""))
		{
			settlement = settlementService.infoSettlement(id);
		}
		request.setAttribute("settlement", settlement);
		return baseUrl+"settlement-input";
	}
	
	@RequestMapping("info")
	public String info(HttpServletRequest request,String id)
	{
		nb_settlement_dict settlement = null;
		if(id!=null && !id.equals(""))
		{
			settlement = settlementService.infoSettlement(id);
			tb_log_opr log = new tb_log_opr();
			log.setModulename(modulename);
			log.setOprcontent("查看"+"["+settlement.getSettlementmethod()+"]"+"结算方式");
			logService.addLog(log);
		}
		request.setAttribute("settlement", settlement);
		return baseUrl+"settlement-info";
	}

	/**
	 * 分页查询结算方式数据字典集合
	 * @param page
	 * @param limit
	 * @return
	 */
	@RequestMapping(value="listData" ,produces = "application/json; charset=utf-8")
	@ResponseBody
	public String listData(int page,int limit)
	{
		Gson gson = new Gson();
		try {
			//System.out.println("page="+page+",limit="+limit);
			Map<String,Object> map = new HashMap<String,Object>();
			//			map.put("page", (page-1)*limit);//mysql
			//			map.put("limit", limit);//mysql
			map.put("page", (page)*limit);//oracle
			map.put("limit", (page-1)*limit);//oracle

			List<nb_settlement_dict> settlement = settlementService.getSettlement(map);
			long count = settlementService.getCount();


			PageListData<nb_settlement_dict> pageData = new PageListData<nb_settlement_dict>();

			pageData.setCode("0");
			pageData.setCount(count);
			pageData.setMessage("");
			pageData.setData(settlement);


			String listJson = gson.toJson(pageData);
			return listJson;
		} catch (Exception e) {
			e.printStackTrace();
			return gson.toJson(new Message("2","操作失败!"));
		}
	}

	@RequestMapping(value="save" ,produces = "application/json; charset=utf-8")
	@ResponseBody
	public String save(nb_settlement_dict settlement)
	{
		Gson gson = new Gson();
		try {
			tb_log_opr log = new tb_log_opr();
			log.setModulename(modulename);
			if(settlement.getId() == null || settlement.getId().equals(""))
			{
				log.setOprcontent("新增"+"["+settlement.getSettlementmethod()+"]"+"结算方式");
			}
			else
			{
				log.setOprcontent("修改"+"["+settlement.getSettlementmethod()+"]"+"结算方式");
			}
			logService.addLog(log);
			int i = settlementService.opentionSettlement(settlement);
			return gson.toJson(""+i);
		} catch (Exception e) {
			e.printStackTrace();
			return gson.toJson("");
		}
	}
	
	@RequestMapping(value="delete" )
	@ResponseBody
	public String delete(String id[])
	{
		Gson gson = new Gson();
		try {
			String paymode = settlementService.infoSettlement(id);
			settlementService.deleteSettlement(id);
			tb_log_opr log = new tb_log_opr();
			log.setModulename(modulename);
			log.setOprcontent("删除"+"["+paymode+"]"+"结算方式");
			logService.addLog(log);
			return gson.toJson(new Message("1","删除成功!"));
		} catch (Exception e) {
			e.printStackTrace();
			return gson.toJson(new Message("2","删除失败!"));
		}
	}
}
