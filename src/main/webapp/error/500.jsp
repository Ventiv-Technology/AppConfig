<%@page import="org.apache.commons.lang.exception.ExceptionUtils"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isErrorPage="true" %>
<%= ExceptionUtils.getRootCauseMessage(exception) %>