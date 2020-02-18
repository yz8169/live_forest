$.ajaxSetup(
    {
        cache: false,
    }
)

function fileExist() {
    var b = false
    $(":file").each(function (i, v) {
        var isvisible = $(this).is(":visible")
        if (isvisible) {
            b = true
            return false
        }
    })
    return b
}

function getShowValue(value) {
    var showValue = value
    if (showValue == "暂无") {
        showValue = "不限"
    }
    return showValue
}

function progressHandlingFunction(e) {
    if (e.lengthComputable) {
        var percent = e.loaded / e.total * 100;
        $('#progress').html("(" + percent.toFixed(2) + "%)");
        if (percent >= 100) {
            $("#info").text("正在运行")
            $("#progress").html("")
        }
    }
}

function dealPredictInfo(data) {
    $("#suggestion1").text(data.pStr)
    $("#charts1").html(data.div)
}

function decimal(data, num) {
    return Math.round(data * Math.pow(10, num)) / Math.pow(10, num)
}

var zhRunning = "正在运行"
var zhInfo = "信息"

function extractor(query) {
    var result = /([^\n]+)$/.exec(query);
    if (result && result[1])
        return result[1].trim();
    return '';
}

var matcherRegex = /[^\n]*$/
var matcherEnd = "\n"
var num = 3

$(function () {

    $("[data-toggle='popover']").popover()

})

