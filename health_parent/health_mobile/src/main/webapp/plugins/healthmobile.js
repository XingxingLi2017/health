// get URL param from locatino bar
// function getUrlParam(paraName) {
//     var url = document.location.toString();
//     //alert(url);
//     var arrObj = url.split("?");
//     if (arrObj.length > 1) {
//         var arrPara = arrObj[1].split("&");
//         var arr;
//         for (var i = 0; i < arrPara.length; i++) {
//             arr = arrPara[i].split("=");
//             if (arr != null && arr[0] == paraName) {
//                 return arr[1];
//             }
//         }
//         return "";
//     }
//     else {
//         return "";
//     }
// }

//get URL param from locatino bar
function getUrlParam(paraName) {
    var url = document.location.toString();
    var exp = "(\\?|&)"+paraName+"=([^&]*)(&|$)";
    var regex = new RegExp(exp, "i");
    var groups = url.match(regex);
    if(groups != null) {
        return groups[2]; // groups[0] is (&|?)paraName=xxx(&|$) , groups[2] is the value
    } else {
        return "";
    }
}

// get current date string
function getToday() {
    var today = new Date();
    var year = today.getFullYear();
    var month = today.getMonth() + 1;
    var day = today.getDate();
    return (year + "-" + month + "-" + day);
}

// get date string after specific days
function getSpecifiedDate(date,days) {
    date.setDate(date.getDate() + days);
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var day = date.getDate();
    return (year + "-" + month + "-" + day);
}

// verify phone number
function checkTelephone(telephone) {
    var reg=/^\d{10}$/;
    if (!reg.test(telephone)) {
        return false;
    } else {
        return true;
    }
}
// verify email
function checkEmail(email) {
    let reg = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;
    if (!reg.test(email)) {
        return false;
    } else {
        return true;
    }
}

// verify id number
function checkIdCard(idCard){
    var reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
    if(reg.test(idCard)){
        return true;
    }else{
        return false;
    }
}

var clock; // interval object
var nums = 30;
var validateCodeButton;
//count down
function doLoop() {
    validateCodeButton.disabled = true;
    nums--;
    if (nums > 0) {
        validateCodeButton.value ='try again after ' + nums + ' seconds';
    } else {
        clearInterval(clock); // clear interval
        validateCodeButton.disabled = false;
        validateCodeButton.value = 'Resend Verification Code';
        nums = 30;
    }
}