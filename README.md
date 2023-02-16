# Stock Analyzer

This is a Java Swing application for analyzing stock data. It loads historical stock data from Yahoo Finance, and displays it in a graphical format to enable analysis. It allows the user to select the date range, as well as the stock to be analyzed.

## Requirements
- Java Development Kit (JDK) 8 or later
- A compatible operating system

## Usage

To run the application, simply compile and run the StockAnalyzer.java file, either using an integrated development environment or the command line. The program will load with a default size of 800x700.

Upon launching the application, the user will be presented with a form allowing them to enter the stock symbol and date range to analyze. Once the information is entered, the application will display a graph of the stock data. The graph will be updated whenever new data is entered.

## Features
- Dynamic graph: The application displays a graph of the historical data for the selected stock, and updates it whenever new data is entered. The graph displays the stock data as a function of date, with the x-axis representing the date and the y-axis representing the either the close value or log(close value) for the stock.

- Interactive interface: The application provides an interactive user interface for entering the date range and stock symbol to be analyzed. The user can specify a start and end date for the analysis, and the application will automatically load the data from Yahoo Finance and display it in the graph.

- The program also displays a graph I call a **"gradient graph"**. The gradient graph shows annual changes in stock by determining if stock tends to rise or fall on a given day.
