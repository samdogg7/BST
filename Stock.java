/**Stock class will store all values associated with each stock.
Author: Sam Doggett
Assignment: Stock
Class: CS 2 ("Data Structures")
*/
public class Stock {
      private String ticker_symbol = null;
      private float price = 0;
      private float original_price;
      private float price_change = 0;
      /**basic variables needed for each stock
      */
      public Stock(String _ticker_symbol, float _starting_price, float _price_change) {
          this.ticker_symbol = _ticker_symbol;
          this.price = _starting_price;
          this.price_change = _price_change;
          this.original_price = _starting_price;
      }
      /**reset the price
      */
      public void resetPrice() {
          price = original_price;
      }
      /**@param change is the price change to add to the current price
      */
      public void updatePrice(float change) {
          price += change;
      }
      /**@return the ticker_symbol
      */
      public String getTicker() {
          return ticker_symbol;
      }
      /**@return the price
      */
      public float getPrice() {
          return price;
      }
      /**@return the price_change
      */
      public float getPriceChange() {
          return price_change;
      }
}
