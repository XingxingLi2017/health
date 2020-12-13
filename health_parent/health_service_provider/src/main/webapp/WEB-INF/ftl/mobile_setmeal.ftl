<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0,user-scalable=no,minimal-ui">
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="icon" href="../img/asset-favico.ico">
    <title>Reservation</title>
    <link rel="stylesheet" href="../css/page-health-order.css" />
    <script src="../plugins/elementui/en.js"></script>
    <script>
        ELEMENT.locale(ELEMENT.lang.en)
    </script>
</head>
<body data-spy="scroll" data-target="#myNavbar" data-offset="150">
<div class="app" id="app">
    <div class="top-header">
        <span class="f-left"><i class="icon-back" onclick="history.go(-1)"></i></span>
        <span class="center">XingHealth</span>
        <span class="f-right"><i class="icon-more"></i></span>
    </div>
    <div class="contentBox">
        <div class="list-column1">
            <ul class="list">
                <#if setmealList??>
                    <#list setmealList as setmeal>
                    <li class="list-item">
                        <a class="link-page" href="mobile_setmeal_detail_${setmeal.id}.html">
                            <img class="img-object f-left"
                                 src="http://qiniu.xingxingli.org/${setmeal.img!}"
                                 alt="">
                            <div class="item-body">
                                <h4 class="ellipsis item-title">${setmeal.name!}</h4>
                                <p class="ellipsis-more item-desc">${setmeal.remark!}</p>
                                <p class="item-keywords">
                                    <#if setmeal.sex??>
                                        <span>
                                        <#if setmeal.sex! == '0'>
                                            M/F
                                        <#else>
                                            <#if setmeal.sex! == '1'>
                                                M
                                            <#else>
                                                F
                                            </#if>
                                        </#if>
                                        </span>
                                    </#if>
                                    <span>${setmeal.age!}</span>
                                </p>
                            </div>
                        </a>
                    </li>
                    </#list>
                </#if>
            </ul>
        </div>
    </div>
</div>
<script src="../plugins/vue/vue.js"></script>
<script src="../plugins/vue/axios-0.18.0.js"></script>
</body>