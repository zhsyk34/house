package com.cat.dao.impl;

import com.cat.dao.HouseDao;
import com.cat.entity.House;
import org.springframework.stereotype.Repository;

@Repository
public class HouseDaoImpl extends CommonDaoImpl<House, Long> implements HouseDao {
}
