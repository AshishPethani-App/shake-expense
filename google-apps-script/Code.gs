/***  Shake Expense - Google Apps Script Backend
 * Deploy as Web App: Execute as Me, Anyone access
 ***/
var SECRET_TOKEN = ''; // Set this to match app token
var SHEET_NAME = 'Expenses';

function doPost(e) {
  try {
    var data = JSON.parse(e.postData.contents);
    if (SECRET_TOKEN && data.token !== SECRET_TOKEN) {
      return respond(false, 'Unauthorized');
    }
    var ss = SpreadsheetApp.getActiveSpreadsheet();
    var sheet = ss.getSheetByName(SHEET_NAME) || createSheet(ss);
    if (sheet.getLastRow() === 0) addHeader(sheet);
    sheet.appendRow([
      data.expenseId || '',
      data.amount || 0,
      data.currency || 'INR',
      data.description || '',
      data.clientTimestamp || new Date().toISOString(),
      new Date().toISOString(),
      'SYNCED'
    ]);
    return respond(true, 'Saved', data.expenseId);
  } catch(err) {
    return respond(false, err.message);
  }
}

function doGet(e) {
  return respond(true, 'Shake Expense API is running');
}

function createSheet(ss) {
  var sheet = ss.insertSheet(SHEET_NAME);
  addHeader(sheet);
  return sheet;
}

function addHeader(sheet) {
  sheet.appendRow(['Expense ID','Amount','Currency','Description','Client Time','Server Time','Status']);
  sheet.getRange(1,1,1,7).setFontWeight('bold').setBackground('#2563EB').setFontColor('#FFFFFF');
}

function respond(success, message, expenseId) {
  return ContentService.createTextOutput(
    JSON.stringify({ success: success, message: message, expenseId: expenseId || null })
  ).setMimeType(ContentService.MimeType.JSON);
}
