//달의 말일 구하기
function numOfDays(year, month) {
    var daysOfMonth;

    if ((month == 4) || (month == 6) || (month == 9) || (month == 11)) {
        daysOfMonth = 30;
    } else {
        daysOfMonth = 31;
        if (month == 2) {
            if (year / 4 - parseInt(year / 4) != 0) {
                daysOfMonth = 28;
            } else {
                if (year / 100 - parseInt(year / 100) != 0) {
                    daysOfMonth = 29;
                } else {
                    if (year / 400 - parseInt(year / 400) != 0) {
                        daysOfMonth = 28;
                    } else {
                        daysOfMonth = 29;
                    }
                }
            }
        }
    }
    return daysOfMonth;
}

function getWeek(date) {
  var tempDate = new Date(date.getFullYear(), date.getMonth(), 1);
  var daysOfMonth = numOfDays(tempDate.getFullYear(), date.getMonth());
  var week = parseInt(((date.getDate() - 1) + tempDate.getDay()) / 7) + 1;



  if(week == 5) {
    if (daysOfMonth == 31 && (tempDate.getDay() == 4 || tempDate.getDay() == 5 || tempDate.getDay() == 6)) {
      return parseInt(((date.getDate() - 1) + tempDate.getDay()) / 7) + 1;
    } else if(daysOfMonth == 30 && (tempDate.getDay() == 5 || tempDate.getDay() == 6)) {
      return parseInt(((date.getDate() - 1) + tempDate.getDay()) / 7) + 1;
    } else if(daysOfMonth == 29 && tempDate.getDay() == 6) {
      return parseInt(((date.getDate() - 1) + tempDate.getDay()) / 7) + 1;
    } else {
      return 0;
    }
  }
  return parseInt(((date.getDate() - 1) + tempDate.getDay()) / 7) + 1;
}

$('#stay_download').on('click', function () {
  var today = new Date();
  var week = getWeek(today);
  if(week == 0) {
    today.setMonth(today.getMonth() + 1);
    week = 1;
  }
  var year = today.getFullYear();
  var month = today.getMonth() + 1;

  location.href = 'http://dsm2015.cafe24.com/stay/download?year=' + year + '&month=' + month + '&week=' + week;
});

$('#goOut_download').on('click', function () {
  var today = new Date();
  var week = getWeek(today);
  if(week == 0) {
    today.setMonth(today.getMonth() + 1);
    week = 1;
  }
  var year = today.getFullYear();
  var month = today.getMonth() + 1;
  
  location.href = 'http://dsm2015.cafe24.com/goingout/download?year=' + year + '&month=' + month + '&week=' + week;
})

$('#extension_download').on('click', function () {
  location.href = 'http://dsm2015.cafe24.com/extension/download';
})