package dkp.algorithm;

import dkp.model.DKPInstance;
import dkp.model.Item;
import dkp.model.ItemSet;
import dkp.model.Solution;

import java.util.ArrayList;
import java.util.List;

/**
 * D{0-1}KP问题求解器
 * 使用动态规划算法求解最优解
 */
public class DKPResolver {
    
    /**
     * 使用动态规划求解D{0-1}KP问题
     * 由于每个项集有3个物品且至多选一个，需要特殊处理
     */
    public static Solution solveByDP(DKPInstance instance) {
        long startTime = System.currentTimeMillis();
        
        int n = instance.getSetCount();
        int C = instance.getCapacity();
        
        // dp[i][w] 表示考虑前i个项集，背包容量为w时的最大价值
        int[][] dp = new int[n + 1][C + 1];
        
        // 记录选择状态，用于回溯
        int[][] choice = new int[n + 1][C + 1];
        
        // 动态规划
        for (int i = 1; i <= n; i++) {
            ItemSet itemSet = instance.getItemSet(i - 1);
            List<Item> items = itemSet.getItems();
            
            for (int w = 0; w <= C; w++) {
                // 不选当前项集的任何一个物品
                dp[i][w] = dp[i - 1][w];
                choice[i][w] = -1;
                
                // 尝试选当前项集的每个物品
                for (int k = 0; k < items.size(); k++) {
                    Item item = items.get(k);
                    if (item.getWeight() <= w) {
                        int newValue = dp[i - 1][w - item.getWeight()] + item.getProfit();
                        if (newValue > dp[i][w]) {
                            dp[i][w] = newValue;
                            choice[i][w] = k;
                        }
                    }
                }
            }
        }
        
        // 回溯构造解
        Solution solution = new Solution(instance);
        int w = C;
        for (int i = n; i >= 1; i--) {
            int selectedItemIndex = choice[i][w];
            if (selectedItemIndex != -1) {
                ItemSet itemSet = instance.getItemSet(i - 1);
                Item selectedItem = itemSet.getItem(selectedItemIndex);
                solution.addItem(selectedItem);
                w -= selectedItem.getWeight();
            }
        }
        
        long endTime = System.currentTimeMillis();
        solution.setSolveTime(endTime - startTime);
        solution.setAlgorithm("Dynamic Programming");
        
        return solution;
    }
    
    /**
     * 使用贪心算法求解(按价值重量比排序)
     * 这不是最优解，但速度快
     */
    public static Solution solveByGreedy(DKPInstance instance) {
        long startTime = System.currentTimeMillis();
        
        int n = instance.getSetCount();
        int C = instance.getCapacity();
        
        // 为每个项集选择最优的物品
        Solution solution = new Solution(instance);
        boolean[] selected = new boolean[n];
        
        // 按价值重量比排序的项集索引
        List<Integer> sortedIndices = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            sortedIndices.add(i);
        }
        
        // 按每个项集中最优物品的价值重量比排序
        sortedIndices.sort((i1, i2) -> {
            double maxRatio1 = getMaxRatio(instance.getItemSet(i1));
            double maxRatio2 = getMaxRatio(instance.getItemSet(i2));
            return Double.compare(maxRatio2, maxRatio1);
        });
        
        int remainingCapacity = C;
        
        for (int idx : sortedIndices) {
            if (selected[idx]) continue;
            
            ItemSet itemSet = instance.getItemSet(idx);
            Item bestItem = null;
            double bestRatio = 0;
            
            // 在当前项集中找满足容量约束且价值重量比最高的物品
            for (Item item : itemSet.getItems()) {
                if (item.getWeight() <= remainingCapacity) {
                    double ratio = item.getProfitWeightRatio();
                    if (ratio > bestRatio) {
                        bestRatio = ratio;
                        bestItem = item;
                    }
                }
            }
            
            // 选择最优物品
            if (bestItem != null) {
                solution.addItem(bestItem);
                remainingCapacity -= bestItem.getWeight();
                selected[idx] = true;
            }
        }
        
        long endTime = System.currentTimeMillis();
        solution.setSolveTime(endTime - startTime);
        solution.setAlgorithm("Greedy");
        
        return solution;
    }
    
    /**
     * 获取项集中物品的最大价值重量比
     */
    private static double getMaxRatio(ItemSet itemSet) {
        double maxRatio = 0;
        for (Item item : itemSet.getItems()) {
            double ratio = item.getProfitWeightRatio();
            if (ratio > maxRatio) {
                maxRatio = ratio;
            }
        }
        return maxRatio;
    }
    
    /**
     * 使用回溯法求解(用于小规模问题)
     */
    public static Solution solveByBacktrack(DKPInstance instance) {
        long startTime = System.currentTimeMillis();
        
        int n = instance.getSetCount();
        int C = instance.getCapacity();
        
        Result result = new Result();
        result.maxProfit = 0;
        result.selected = new int[n];
        
        backtrack(instance, 0, 0, 0, new int[n], result, C);
        
        // 构造解
        Solution solution = new Solution(instance);
        for (int i = 0; i < n; i++) {
            if (result.selected[i] != -1) {
                ItemSet itemSet = instance.getItemSet(i);
                solution.addItem(itemSet.getItem(result.selected[i]));
            }
        }
        
        long endTime = System.currentTimeMillis();
        solution.setSolveTime(endTime - startTime);
        solution.setAlgorithm("Backtrack");
        
        return solution;
    }
    
    /**
     * 回溯辅助类
     */
    private static class Result {
        int maxProfit;
        int[] selected;
    }
    
    /**
     * 回溯算法
     */
    private static void backtrack(DKPInstance instance, int setIndex, int currentWeight, 
                                   int currentProfit, int[] currentSelect, 
                                   Result result, int capacity) {
        if (setIndex == instance.getSetCount()) {
            if (currentProfit > result.maxProfit) {
                result.maxProfit = currentProfit;
                System.arraycopy(currentSelect, 0, result.selected, 0, currentSelect.length);
            }
            return;
        }
        
        ItemSet itemSet = instance.getItemSet(setIndex);
        
        // 不选当前项集的任何物品
        currentSelect[setIndex] = -1;
        backtrack(instance, setIndex + 1, currentWeight, currentProfit, 
                  currentSelect, result, capacity);
        
        // 尝试选当前项集的每个物品
        for (int i = 0; i < itemSet.getItems().size(); i++) {
            Item item = itemSet.getItem(i);
            if (currentWeight + item.getWeight() <= capacity) {
                currentSelect[setIndex] = i;
                backtrack(instance, setIndex + 1, 
                         currentWeight + item.getWeight(), 
                         currentProfit + item.getProfit(), 
                         currentSelect, result, capacity);
            }
        }
    }
    
    /**
     * 求解最优解(默认使用动态规划)
     */
    public static Solution solve(DKPInstance instance) {
        return solveByDP(instance);
    }
}
