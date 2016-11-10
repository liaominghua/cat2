<%@ tag trimDirectiveWhitespaces="true" pageEncoding="UTF-8"%>
<%@ taglib prefix="d" uri="/WEB-INF/document.tld"%>
<%@ attribute name="resource" fragment="true"%>

<html lang="en">

<head>
	<title>CAT</title>
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<meta charset="utf-8">
	<meta name="description" content="CAT Districuted Realtime Monitoring System">
	<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0">
	<link rel="stylesheet" href="${model.webapp}/assets/css/bootstrap.min.css">
	<link rel="stylesheet" href="${model.webapp}/assets/css/font-awesome.min.css">
	<link rel="stylesheet" href="${model.webapp}/assets/css/jquery-ui.min.css">
	<link rel="stylesheet" href="${model.webapp}/assets/css/ace-fonts.css">
	<link rel="stylesheet" href="${model.webapp}/assets/css/ace.min.css" id="main-ace-style">
	<link rel="stylesheet" href="${model.webapp}/assets/css/ace-skins.min.css">
	<link rel="stylesheet" href="${model.webapp}/assets/css/ace-rtl.min.css">
	<link rel="stylesheet" href="${model.webapp}/css/body.css">
	<script src="${model.webapp}/js/jquery-1.7.1.js"></script>
	<script src="${model.webapp}/assets/js/ace-extra.min.js"></script>
	<script src="${model.webapp}/assets/js/bootstrap.min.js"></script>
	<script src="${model.webapp}/js/highcharts.js"></script>
	<script src="${model.webapp}/assets/js/jquery-ui.min.js"></script>
	<script src="${model.webapp}/assets/js/jquery.ui.touch-punch.min.js"></script>
	<script src="${model.webapp}/assets/js/ace-elements.min.js"></script>
	<script src="${model.webapp}/assets/js/ace.min.js"></script>
	
	<jsp:invoke fragment="resource" />
</head>

<body class="no-skin">
	<d:document-header />

	<div class="main-container" id="main-container">
		<d:document-navbar />
		<d:document-content>
			<jsp:body><jsp:doBody /></jsp:body>
		</d:document-content>
	</div>
	</div>
</body>

</html>
