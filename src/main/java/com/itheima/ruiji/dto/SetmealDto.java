package com.itheima.ruiji.dto;

import com.itheima.ruiji.entity.Setmeal;
import com.itheima.ruiji.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
