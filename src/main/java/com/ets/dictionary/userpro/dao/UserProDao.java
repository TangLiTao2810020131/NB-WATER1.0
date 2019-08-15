package com.ets.dictionary.userpro.dao;

import java.util.List;
import java.util.Map;
import com.ets.dictionary.userpro.entity.nb_userpro_dict;

/**
 * 用户性质字典表链接数据库操作类
 * @author WH
 *
 */
public interface UserProDao {

	/**
	 * 根据条件查询用户性质字典表
	 * @param map
	 * @return
	 */
	List<nb_userpro_dict> selectUserPro(Map<String, Object> map);

	/**
	 * 根据条件查询用户性质字典表总数
	 * @return
	 */
	long selectCount();

	/**
	 * 更新用户性质字段数据
	 * @param UserPro
	 */
	void updateUserPro(nb_userpro_dict userpro);

	/**
	 * 添加用户性质字段数据
	 * @param UserPro
	 */
	void insertUserPro(nb_userpro_dict userpro);

	/**
	 * 根据ID查询用户性质数据字典信息
	 * @param id
	 * @return
	 */
	nb_userpro_dict infoUserPro(String id);

	/**
	 * 根据ID删除用户性质数据字典信息
	 * @param id
	 */
	void deleteUserPro(String[] id);

	List<nb_userpro_dict> infoUserProList(String[] id);

	List<nb_userpro_dict> selectUserProAll();


}
