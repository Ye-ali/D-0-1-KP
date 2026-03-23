package dkp.model;

/**
 * 物品类
 * 表示D{0-1}KP问题中的一个物品
 */
public class Item {
    private int id;           // 物品编号
    private int profit;       // 价值
    private int weight;       // 重量
    private int setIndex;     // 所属项集索引
    private int itemIndex;    // 在项集中的位置(0, 1, 2)
    
    public Item(int id, int profit, int weight, int setIndex, int itemIndex) {
        this.id = id;
        this.profit = profit;
        this.weight = weight;
        this.setIndex = setIndex;
        this.itemIndex = itemIndex;
    }
    
    public int getId() {
        return id;
    }
    
    public int getProfit() {
        return profit;
    }
    
    public int getWeight() {
        return weight;
    }
    
    public int getSetIndex() {
        return setIndex;
    }
    
    public int getItemIndex() {
        return itemIndex;
    }
    
    /**
     * 计算价值重量比
     */
    public double getProfitWeightRatio() {
        if (weight == 0) return 0;
        return (double) profit / weight;
    }
    
    @Override
    public String toString() {
        return String.format("Item[%d](P:%d, W:%d, Set:%d, Pos:%d)", 
            id, profit, weight, setIndex, itemIndex);
    }
}
