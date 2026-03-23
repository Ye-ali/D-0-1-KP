package dkp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 项集类
 * D{0-1}KP问题中，每个项集包含3个物品
 * 其中第3个物品的价值=前两个物品价值之和
 * 第3个物品的重量<前两个物品重量之和
 */
public class ItemSet {
    private int index;                  // 项集索引
    private List<Item> items;           // 项集中的3个物品
    
    public ItemSet(int index) {
        this.index = index;
        this.items = new ArrayList<>(3);
    }
    
    public void addItem(Item item) {
        if (items.size() < 3) {
            items.add(item);
        }
    }
    
    public int getIndex() {
        return index;
    }
    
    public List<Item> getItems() {
        return items;
    }
    
    /**
     * 获取项集中第3个物品的价值重量比
     */
    public double getThirdItemRatio() {
        if (items.size() < 3) return 0;
        Item thirdItem = items.get(2);
        return thirdItem.getProfitWeightRatio();
    }
    
    /**
     * 获取项集中指定位置的物品
     */
    public Item getItem(int position) {
        if (position >= 0 && position < items.size()) {
            return items.get(position);
        }
        return null;
    }
    
    /**
     * 验证项集是否满足D{0-1}KP问题的约束条件
     */
    public boolean validate() {
        if (items.size() != 3) return false;
        
        Item item0 = items.get(0);
        Item item1 = items.get(1);
        Item item2 = items.get(2);
        
        // 验证: 第3个物品的价值 = 前两个物品价值之和
        boolean profitValid = item2.getProfit() == item0.getProfit() + item1.getProfit();
        
        // 验证: 第3个物品的重量 < 前两个物品重量之和
        boolean weightValid = item2.getWeight() < item0.getWeight() + item1.getWeight();
        
        return profitValid && weightValid;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ItemSet[").append(index).append("]:\n");
        for (int i = 0; i < items.size(); i++) {
            sb.append("  ").append(items.get(i)).append("\n");
        }
        return sb.toString();
    }
}
