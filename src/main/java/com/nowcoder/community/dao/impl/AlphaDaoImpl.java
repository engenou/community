package com.nowcoder.community.dao.impl;

import com.nowcoder.community.dao.AlphaDao;
import org.springframework.stereotype.Repository;

/**
 * @author Eugen
 * @creat 2022-04-30 17:13
 */
@Repository
public class AlphaDaoImpl implements AlphaDao {

    @Override
    public String select() {
        return "Mybatis";
    }
}
