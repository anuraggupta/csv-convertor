import com.opencsv.CSVReaderBuilder
import com.opencsv.CSVWriter
import com.opencsv.CSVWriterBuilder
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

val ZB_DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss a")
val ZB_AC_STMT_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
val WX_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
val KOINLY_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
val CT_DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")

const val KOINLY_DATE = 0
const val KOINLY_SENT_AMOUNT = 1
const val KOINLY_SENT_CURRENCY = 2
const val KOINLY_RECEIVED_AMOUNT = 3
const val KOINLY_RECEIVED_CURRENCY = 4
const val KOINLY_FEE_AMOUNT = 5
const val KOINLY_FEE_CURRENCY = 6
const val KOINLY_NET_WORTH_AMOUNT = 7
const val KOINLY_NET_WORTH_CURRENCY = 8
const val KOINLY_LABEL = 9
const val KOINLY_DESCRIPTION = 10
const val KOINLY_TXHASH = 11


const val CT_DATE = 0
const val CT_RECVD_QTY = 1
const val CT_RECVD_CURRENCY = 2
const val CT_SENT_QTY = 3
const val CT_SENT_CURRENCY = 4
const val CT_FEE = 5
const val CT_FEE_CURRENCY = 6
const val CT_TAG = 7
val CT_HEADER = arrayOf(
    "Date",
    "Received Quantity",
    "Received Currency",
    "Sent Quantity",
    "Sent Currency",
    "Fee Amount",
    "Fee Currency",
    "Tag"
)
val KOINLY_HEADER = arrayOf(
    "Date",
    "Sent Amount",
    "Sent Currency",
    "Received Amount",
    "Received Currency",
    "Fee Amount",
    "Fee Currency",
    "Net Worth Amount",
    "Net Worth Currency",
    "Label",
    "Description",
    "TxHash"
)

const val ZB_ORDER_ID = 0
const val ZB_ORDER_DATE_TIME = 1
const val ZB_PAIR = 2
const val ZB_BUY_SELL = 3
const val ZB_ORDER_PRICE = 4
const val ZB_ORDER_QTY = 5
const val ZB_TRADE_VALUE = 6
const val ZB_MAKER_TAKER_FEE = 7
const val ZB_INTRADAY_FEE = 8

const val ZB_AC_STMT_DATE = 0
const val ZB_AC_STMT_CURRENCY = 1
const val ZB_AC_STMT_FROM_NAME = 2
const val ZB_AC_STMT_TO_NAME = 3
const val ZB_AC_STMT_INVOICE = 4
const val ZB_AC_STMT_TRANSACTION_TYPE = 5
const val ZB_AC_STMT_STATUS = 6
const val ZB_AC_STMT_QUANTITY = 7
const val ZB_AC_STMT_RATE = 8
const val ZB_AC_STMT_YIELD_RATE = 9
const val ZB_AC_STMT_CRYPTO_FIAT_FEES = 10
const val ZB_AC_STMT_CRYPTO_USER_AMOUNT = 11
const val ZB_AC_STMT_CRYPTO_MAKER_FEES = 12
const val ZB_AC_STMT_CRYPTO_TAKER_FEES = 13
const val ZB_AC_STMT_CRYPTO_INTRADAY_FEES = 14
const val ZB_AC_STMT_CRYPTO_TOTAL_FEES = 15
const val ZB_AC_STMT_CRYPTO_TXID = 16
const val ZB_AC_STMT_CRYPTO_REMARKS = 17


const val WX_SHEET_EXCHANGE_TRADES = 1
const val WX_SHEET_P2P_TRADES = 2
const val WX_SHEET_DEPOSITS_AND_WITHDRAWALS = 3
const val WX_SHEET_THIRD_PARTY_TRANSFERS = 8

const val WX_ET_DATE = 0
const val WX_ET_MARKET = 1
const val WX_ET_PRICE = 2
const val WX_ET_VOLUME = 3
const val WX_ET_TOTAL = 4
const val WX_ET_TRADE = 5
const val WX_ET_FEE_CURRENCY = 6
const val WX_ET_FEE = 7


fun main(args: Array<String>) {


    println("Hello World!")
//    zebpayCustomerAccountStatement2CoinTracker("/Users/anuragup/Downloads/Account Statement (1).csv")
//    zebpayCustomerAccountStatement2Koinly("/Users/anuragup/Downloads/Account Statement (1).csv")
//    zebpay2CoinTracker("/Users/anuragup/Downloads/temp/trade/Trade-Statement-991082665505102021055412.csv")
    wazirx2CoinTracker("/Users/anuragup/Downloads/temp/trade/wazirx/WazirX_TradeReport_2020-04-01_2021-03-31.xlsx")
    wazirx2CoinTracker("/Users/anuragup/Downloads/temp/trade/wazirx/WazirX_TradeReport_2021-04-01_2022-03-31.xlsx")
    wazirx2CoinTracker("/Users/anuragup/Downloads/temp/trade/wazirx/WazirX_TradeReport_2022-04-01_2022-06-11.xlsx")


}

private fun wazirx2CoinTracker(filepath: String) {

    val resultCSVPath = getResultCSVPath(filepath)
    val csvWriter =
        CSVWriterBuilder(FileWriter(resultCSVPath))
            .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
            .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER)
            .withEscapeChar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
            .withLineEnd(CSVWriter.DEFAULT_LINE_END).build()
    csvWriter.writeNext(CT_HEADER)


    val inputStream = FileInputStream(filepath)
    //Instantiate Excel workbook using existing file:
    val xlWb = WorkbookFactory.create(inputStream)

    //Get reference to first sheet:
    val xlWs = xlWb.getSheetAt(WX_SHEET_EXCHANGE_TRADES)
    var rowData = xlWs.getRow(1)
    var row = 1
    while (rowData != null) {
        val out = arrayOfNulls<String>(8)

        out[CT_DATE] = getDate(rowData.getCell(WX_ET_DATE).stringCellValue, WX_DATE_FORMAT, CT_DATE_FORMAT)

        if ("Buy" == rowData.getCell(WX_ET_TRADE).stringCellValue) {

            out[CT_RECVD_QTY] = rowData.getCell(WX_ET_VOLUME).numericCellValue.toString()
            out[CT_RECVD_CURRENCY] = rowData.getCell(WX_ET_MARKET).stringCellValue.substring(
                0,
                rowData.getCell(WX_ET_MARKET).stringCellValue.length - rowData.getCell(WX_ET_FEE_CURRENCY).stringCellValue.length
            )
            out[CT_SENT_QTY] = rowData.getCell(WX_ET_TOTAL).numericCellValue.toString()
            out[CT_SENT_CURRENCY] = rowData.getCell(WX_ET_FEE_CURRENCY).stringCellValue

        } else if ("Sell" == rowData.getCell(WX_ET_TRADE).stringCellValue) {

            out[CT_RECVD_QTY] = rowData.getCell(WX_ET_TOTAL).numericCellValue.toString()
            out[CT_RECVD_CURRENCY] = rowData.getCell(WX_ET_FEE_CURRENCY).stringCellValue

            out[CT_SENT_QTY] = rowData.getCell(WX_ET_VOLUME).numericCellValue.toString()
            out[CT_SENT_CURRENCY] = rowData.getCell(WX_ET_MARKET).stringCellValue.substring(
                0,
                rowData.getCell(WX_ET_MARKET).stringCellValue.length - rowData.getCell(WX_ET_FEE_CURRENCY).stringCellValue.length
            )

        }

        out[CT_FEE] = rowData.getCell(WX_ET_FEE).numericCellValue.toString()
        out[CT_FEE_CURRENCY] = rowData.getCell(WX_ET_FEE_CURRENCY).stringCellValue

        csvWriter.writeNext(out)
        print("CONVERTED : ")
        for (column in out) {
            print(column + ",")
        }
        println()
        row++
        rowData = xlWs.getRow(row)
    }
    csvWriter.close()
    xlWb.close()
}

private fun getResultCSVPath(filepath: String): String {
    val path = Paths.get(filepath)
    val fileName = path.fileName.toString().substring(0, path.fileName.toString().lastIndexOf(".")) + "-CT.csv"
    return path.parent.toString() + File.separator + fileName
}

private fun zebpay2CoinTracker(filePath: String) {

    val resultCSVPath = getResultCSVPath(filePath)
    val csvReader =
        CSVReaderBuilder(FileReader(filePath))
            .build()
    val csvWriter =
        CSVWriterBuilder(FileWriter(resultCSVPath))
            .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
            .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER)
            .withEscapeChar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
            .withLineEnd(CSVWriter.DEFAULT_LINE_END).build()
    csvWriter.writeNext(CT_HEADER)

// Maybe do something with the header if there is one
    val header = csvReader.readNext()

// Read the rest
    var line: Array<String>? = csvReader.readNext()
    while (line != null && line.size == 9) {
        // Do something with the data
        //Date	Received Quantity	Received Currency	Sent Quantity	Sent Currency	Fee Amount	Fee Currency	Tag
        val out = arrayOfNulls<String>(8)
        val date = getDate(line[1], ZB_DATE_FORMAT, CT_DATE_FORMAT)
        out[CT_DATE] = date



        if ("BUY" == line[ZB_BUY_SELL]) {
            out[CT_RECVD_QTY] = line[ZB_ORDER_QTY]
            out[CT_RECVD_CURRENCY] = line[ZB_PAIR].split("-")[0]
            out[CT_SENT_QTY] = line[ZB_TRADE_VALUE]
            out[CT_SENT_CURRENCY] = line[ZB_PAIR].split("-")[1]
        } else if ("SELL" == line[ZB_BUY_SELL]) {
            out[CT_RECVD_QTY] = line[ZB_TRADE_VALUE]
            out[CT_RECVD_CURRENCY] = line[ZB_PAIR].split("-")[1]
            out[CT_SENT_QTY] = line[ZB_ORDER_QTY]
            out[CT_SENT_CURRENCY] = line[ZB_PAIR].split("-")[0]
        }

        out[CT_FEE] = (line[ZB_INTRADAY_FEE].toDouble() + line[ZB_MAKER_TAKER_FEE].toDouble()).toString()
        out[CT_FEE_CURRENCY] = line[ZB_PAIR].split("-")[1]

        print("CONVERTED : ")
        for (column in out) {
            print(column + ",")
        }
        println()

        csvWriter.writeNext(out)
        line = csvReader.readNext()
    }

    csvReader.close()
    csvWriter.close()
}


private fun zebpayCustomerAccountStatement2Koinly(filePath: String) {

    val resultCSVPath = getResultCSVPath(filePath)
    val csvReader =
        CSVReaderBuilder(FileReader(filePath))
            .build()
    val csvWriter =
        CSVWriterBuilder(FileWriter(resultCSVPath))
            .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
            .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER)
            .withEscapeChar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
            .withLineEnd(CSVWriter.DEFAULT_LINE_END).build()
    csvWriter.writeNext(KOINLY_HEADER)

// Maybe do something with the header if there is one
    val header = csvReader.readNext()

// Read the rest
    var line: Array<String>? = csvReader.readNext()
    while (line != null) {
        // Do something with the data
        //Date	Received Quantity	Received Currency	Sent Quantity	Sent Currency	Fee Amount	Fee Currency	Tag
        if ("Welcome" == line[ZB_AC_STMT_TRANSACTION_TYPE] || "Buy" == line[ZB_AC_STMT_TRANSACTION_TYPE] || "Sell" == line[ZB_AC_STMT_TRANSACTION_TYPE] || "Earnings Credited" == line[ZB_AC_STMT_TRANSACTION_TYPE]) {
            val out = arrayOfNulls<String>(8)
            val date = getIST2UTCDate(line[ZB_AC_STMT_DATE], ZB_AC_STMT_DATE_FORMAT, KOINLY_DATE_FORMAT)
            out[KOINLY_DATE] = date

            if ("Buy" == line[ZB_AC_STMT_TRANSACTION_TYPE]) {
                out[KOINLY_RECEIVED_AMOUNT] = line[ZB_AC_STMT_QUANTITY]
                out[KOINLY_RECEIVED_CURRENCY] = line[ZB_AC_STMT_CURRENCY].replace("+AC0-", "-").split("-")[0]
                out[KOINLY_SENT_AMOUNT] = line[ZB_AC_STMT_CRYPTO_USER_AMOUNT]
                out[KOINLY_SENT_CURRENCY] = line[ZB_AC_STMT_CURRENCY].replace("+AC0-", "-").split("-")[1]
                if ("-".equals(line[ZB_AC_STMT_CRYPTO_TOTAL_FEES])) {
                    out[KOINLY_FEE_AMOUNT] = "0".toDouble().toString()
                } else {
                    out[KOINLY_FEE_AMOUNT] = line[ZB_AC_STMT_CRYPTO_TOTAL_FEES].toDouble().toString()
                }
                out[KOINLY_FEE_CURRENCY] = line[ZB_AC_STMT_CURRENCY].replace("+AC0-", "-").split("-")[1]

            } else if ("Sell" == line[ZB_AC_STMT_TRANSACTION_TYPE]) {
                out[KOINLY_RECEIVED_AMOUNT] = line[ZB_AC_STMT_CRYPTO_USER_AMOUNT]
                out[KOINLY_RECEIVED_CURRENCY] = line[ZB_AC_STMT_CURRENCY].replace("+AC0-", "-").split("-")[1]
                out[KOINLY_SENT_AMOUNT] = line[ZB_AC_STMT_QUANTITY]
                out[KOINLY_SENT_CURRENCY] = line[ZB_AC_STMT_CURRENCY].replace("+AC0-", "-").split("-")[0]
                if ("-".equals(line[ZB_AC_STMT_CRYPTO_TOTAL_FEES])) {
                    out[KOINLY_FEE_AMOUNT] = "0".toDouble().toString()
                } else {
                    out[KOINLY_FEE_AMOUNT] = line[ZB_AC_STMT_CRYPTO_TOTAL_FEES].toDouble().toString()
                }
                out[KOINLY_FEE_CURRENCY] = line[ZB_AC_STMT_CURRENCY].replace("+AC0-", "-").split("-")[1]
            } else if ("Earnings Credited" == line[ZB_AC_STMT_TRANSACTION_TYPE] || "Welcome" == line[ZB_AC_STMT_TRANSACTION_TYPE]) {
                out[KOINLY_RECEIVED_AMOUNT] = line[ZB_AC_STMT_QUANTITY]
                out[KOINLY_RECEIVED_CURRENCY] = line[ZB_AC_STMT_CURRENCY]
            }
            print("CONVERTED : ")
            for (column in out) {
                print(column + ",")
            }
            println()

            csvWriter.writeNext(out)
        }

        line = csvReader.readNext()
    }

    csvReader.close()
    csvWriter.close()
}

private fun zebpayCustomerAccountStatement2CoinTracker(filePath: String) {

    val resultCSVPath = getResultCSVPath(filePath)
    val csvReader =
        CSVReaderBuilder(FileReader(filePath))
            .build()
    val csvWriter =
        CSVWriterBuilder(FileWriter(resultCSVPath))
            .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
            .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER)
            .withEscapeChar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
            .withLineEnd(CSVWriter.DEFAULT_LINE_END).build()
    csvWriter.writeNext(CT_HEADER)

// Maybe do something with the header if there is one
    val header = csvReader.readNext()

// Read the rest
    var line: Array<String>? = csvReader.readNext()
    while (line != null) {
        // Do something with the data
        //Date	Received Quantity	Received Currency	Sent Quantity	Sent Currency	Fee Amount	Fee Currency	Tag
        if ("Welcome" == line[ZB_AC_STMT_TRANSACTION_TYPE] || "Buy" == line[ZB_AC_STMT_TRANSACTION_TYPE] || "Sell" == line[ZB_AC_STMT_TRANSACTION_TYPE] || "Earnings Credited" == line[ZB_AC_STMT_TRANSACTION_TYPE]) {
            val out = arrayOfNulls<String>(8)
            val date = getIST2UTCDate(line[ZB_AC_STMT_DATE], ZB_AC_STMT_DATE_FORMAT, CT_DATE_FORMAT)
            out[CT_DATE] = date

            if ("Buy" == line[ZB_AC_STMT_TRANSACTION_TYPE]) {
                out[CT_RECVD_QTY] = line[ZB_AC_STMT_QUANTITY]
                out[CT_RECVD_CURRENCY] = line[ZB_AC_STMT_CURRENCY].replace("+AC0-", "-").split("-")[0]
                out[CT_SENT_QTY] = line[ZB_AC_STMT_CRYPTO_USER_AMOUNT]
                out[CT_SENT_CURRENCY] = line[ZB_AC_STMT_CURRENCY].replace("+AC0-", "-").split("-")[1]
                if ("-".equals(line[ZB_AC_STMT_CRYPTO_TOTAL_FEES])) {
                    out[CT_FEE] = "0".toDouble().toString()
                } else {
                    out[CT_FEE] = line[ZB_AC_STMT_CRYPTO_TOTAL_FEES].toDouble().toString()
                }
                out[CT_FEE_CURRENCY] = line[ZB_AC_STMT_CURRENCY].replace("+AC0-", "-").split("-")[1]

            } else if ("Sell" == line[ZB_AC_STMT_TRANSACTION_TYPE]) {
                out[CT_RECVD_QTY] = line[ZB_AC_STMT_CRYPTO_USER_AMOUNT]
                out[CT_RECVD_CURRENCY] = line[ZB_AC_STMT_CURRENCY].replace("+AC0-", "-").split("-")[1]
                out[CT_SENT_QTY] = line[ZB_AC_STMT_QUANTITY]
                out[CT_SENT_CURRENCY] = line[ZB_AC_STMT_CURRENCY].replace("+AC0-", "-").split("-")[0]
                if ("-".equals(line[ZB_AC_STMT_CRYPTO_TOTAL_FEES])) {
                    out[CT_FEE] = "0".toDouble().toString()
                } else {
                    out[CT_FEE] = line[ZB_AC_STMT_CRYPTO_TOTAL_FEES].toDouble().toString()
                }
                out[CT_FEE_CURRENCY] = line[ZB_AC_STMT_CURRENCY].replace("+AC0-", "-").split("-")[1]
            } else if ("Earnings Credited" == line[ZB_AC_STMT_TRANSACTION_TYPE] || "Welcome" == line[ZB_AC_STMT_TRANSACTION_TYPE]) {
                out[CT_RECVD_QTY] = line[ZB_AC_STMT_QUANTITY]
                out[CT_RECVD_CURRENCY] = line[ZB_AC_STMT_CURRENCY]
            }
            print("CONVERTED : ")
            for (column in out) {
                print(column + ",")
            }
            println()

            csvWriter.writeNext(out)
        }

        line = csvReader.readNext()
    }

    csvReader.close()
    csvWriter.close()
}

private fun getDate(s: String, inPattern: DateTimeFormatter, outPattern: DateTimeFormatter): String {
    val date = LocalDateTime.parse(s, inPattern)
    return date.format(outPattern)
}

private fun getIST2UTCDate(s: String, inPattern: DateTimeFormatter, outPattern: DateTimeFormatter): String {
    val date = LocalDateTime.parse(s, inPattern)
    val istDate = ZonedDateTime.of(date, ZoneId.of("Asia/Kolkata")).toInstant()
    val utcDate = istDate.atZone(ZoneId.of("UTC"))
    return outPattern.format(utcDate)
}


