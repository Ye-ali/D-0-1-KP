package dkp.utils;

import dkp.model.DKPInstance;
import dkp.model.ItemSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 排序类
 * 按项集第三项的价值:重量比进行非递增排序
 */
public class Sorter {
    
    /**
     * 按第三项的价值重量比进行非递增排序
     * @param instance DKP实例
     * @return 排序后的项集列表
     */
    public static List<ItemSet> sortByThirdItemRatio(DKPInstance instance) {
        List<ItemSet> sortedList = new ArrayList<>(instance.getItemSets());
        
        Collections.sort(sortedList, new Comparator<ItemSet>() {
            @Override
            public int compare(ItemSet s1, ItemSet s2) {
                double ratio1 = s1.getThirdItemRatio();
                double ratio2 = s2.getThirdItemRatio();
                // 非递增排序(从大到小)
                return Double.compare(ratio2, ratio1);
            }
        });
        
        return sortedList;
    }
    
    /**
     * 获取排序后的详细信息
     */
    public static String getSortedDetails(DKPInstance instance) {
        List<ItemSet> sortedList = sortByThirdItemRatio(instance);
        StringBuilder sb = new StringBuilder();
        
        sb.append("========== Sorted by Third Item Ratio (Non-increasing) ==========\n");
        sb.append(String.format("%-10s %-15s %-15s %-15s\n", 
            "SetIndex", "ThirdProfit", "ThirdWeight", "Ratio"));
        sb.append("----------------------------------------------------------------\n");
        
        for (ItemSet set : sortedList) {
            if (set.getItems().size() >= 3) {
                int profit = set.getItem(2).getProfit();
                int weight = set.getItem(2).getWeight();
                double ratio = set.getThirdItemRatio();
                
                sb.append(String.format("%-10d %-15d %-15d %-15.6f\n", 
                    set.getIndex(), profit, weight, ratio));
            }
        }
        
        sb.append("================================================================\n");
        return sb.toString();
    }
    
    /**
     * 打印排序结果
     */
    public static void printSorted(DKPInstance instance) {
        System.out.println(getSortedDetails(instance));
    }
}
