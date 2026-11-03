<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<header class="student-portal-header">

    <div class="student-portal-branding">

        <c:if test="${param.dashboardSidebar eq 'true'}">
            <button class="student-sidebar-toggle"
                    id="sidebarToggle"
                    type="button"
                    aria-label="Hide dashboard sidebar"
                    aria-controls="dashboardSidebar"
                    aria-expanded="true">
                <span></span>
                <span></span>
                <span></span>
            </button>
        </c:if>

        <a href="${pageContext.request.contextPath}/home"
           class="student-portal-brand">

            <img src="${pageContext.request.contextPath}/assets/images/logo.png"
                 alt="My Internship Technologies Logo">

            <span>My Internship Technologies</span>
        </a>
    </div>

    <button class="student-portal-menu-toggle"
            id="studentNavToggle"
            type="button"
            aria-label="Toggle navigation"
            aria-controls="studentPortalMenu"
            aria-expanded="false">

        <span></span>
        <span></span>
        <span></span>
    </button>

    <nav class="student-portal-menu"
         id="studentPortalMenu"
         aria-label="Student navigation">

        <a href="${pageContext.request.contextPath}/home"
           class="student-portal-link${param.active eq 'home' ? ' active' : ''}">
            Home
        </a>

        <a href="${pageContext.request.contextPath}/dashboard"
           class="student-portal-link${param.active eq 'dashboard' ? ' active' : ''}">
            My Dashboard
        </a>

        <a href="${pageContext.request.contextPath}/profile"
           class="student-portal-link${param.active eq 'profile' ? ' active' : ''}">
            Profile
        </a>

        <a href="${pageContext.request.contextPath}/assignments"
           class="student-portal-link${param.active eq 'assignments' ? ' active' : ''}">
            Assignments
        </a>

        <a href="${pageContext.request.contextPath}/courses"
           class="student-portal-link${param.active eq 'courses' ? ' active' : ''}">
            Video Courses
        </a>

        <a href="${pageContext.request.contextPath}/lectures"
           class="student-portal-link${param.active eq 'lectures' ? ' active' : ''}">
            Lectures
        </a>

        <a href="${pageContext.request.contextPath}/question-bank"
           class="student-portal-link${param.active eq 'question-bank' ? ' active' : ''}">
            Question Bank
        </a>

        <a href="${pageContext.request.contextPath}/resume-analysis"
           class="student-portal-link${param.active eq 'resume-analysis' ? ' active' : ''}">
            Resume Analysis
        </a>

        <span class="student-portal-greeting">
            Hi,
            <c:choose>
                <c:when test="${not empty sessionScope.userFirstName}">
                    <c:out value="${sessionScope.userFirstName}"/>
                </c:when>
                <c:when test="${not empty sessionScope.user.firstName}">
                    <c:out value="${sessionScope.user.firstName}"/>
                </c:when>
                <c:otherwise>Student</c:otherwise>
            </c:choose>
        </span>

        <a href="${pageContext.request.contextPath}/logout"
           class="student-portal-logout">
            Logout
        </a>

    </nav>

</header>

<script>
(function () {

    var toggle = document.getElementById("studentNavToggle");
    var menu = document.getElementById("studentPortalMenu");

    if (toggle && menu) {
        toggle.addEventListener("click", function () {

            var expanded = toggle.getAttribute("aria-expanded") !== "true";

            toggle.setAttribute("aria-expanded", String(expanded));
            menu.classList.toggle("is-open", expanded);

        });
    }

    var sidebarToggle = document.getElementById("sidebarToggle");

    if (sidebarToggle) {
        sidebarToggle.addEventListener("click", function () {

            var sidebar = document.getElementById("dashboardSidebar");

            if (sidebar) {
                sidebar.classList.toggle("is-collapsed");

                var expanded = !sidebar.classList.contains("is-collapsed");

                sidebarToggle.setAttribute(
                    "aria-expanded",
                    String(expanded)
                );

                sidebarToggle.setAttribute(
                    "aria-label",
                    expanded ? "Hide dashboard sidebar" : "Show dashboard sidebar"
                );
            }

        });
    }

})();
</script>