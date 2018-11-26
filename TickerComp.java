import java.util.Comparator;

public class TickerComp implements Comparator<Stock>{
    int count = 0;
    @Override
    public int compare(Stock stock_update, Stock stock) {
        int comp_num = stock_update.getTicker().compareTo(stock.getTicker());
        //only execute this stuff if it is a stock with a price of -999 (only in price_updates)
        if(stock_update.getPrice() == -999) {
            if(comp_num == 0) {
                float price_change = stock_update.getPriceChange();
                stock.updatePrice(price_change);
                return 0;
            }
        }
        return comp_num;
    }
}
