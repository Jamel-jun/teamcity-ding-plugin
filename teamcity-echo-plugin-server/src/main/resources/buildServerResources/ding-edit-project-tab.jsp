<%@ page pageEncoding="UTF-8"%>
<%@ include file="/include.jsp"%>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<c:url value="/admin/deploys-edit-project.html" var="actionUrl" />


<bs:linkScript>
    ${teamcityPluginResourcesPath}js/ding-edit-project-config.js
</bs:linkScript>

<form
      id="deployDashboardForm"
      method="post"
      action="${actionUrl}"
      onsubmit="return saveDeploymentDashboardConfig(this, '${actionUrl}')">

  <div class="editDeploySettingsPage">

    <h2 class="noBorder">钉钉机器人通知</h2>
    <%--<div class="grayNote">
      ${actionUrl}----${projectExternalId}
      Defines what configuration properties are used from builds to get project and environment
      information for project '${projectExternalId}' and sub-projects in order to generate the
      deployment dashboard.
    </div>

    <p>
      Note that only <i>Deployment</i> builds will be shown on the dashboard, not regular builds.
      For more information, see the <a href="https://github.com/vyadh/teamcity-deployment-dashboard/tree/master#deployment-visibility">Deployment Visibility</a>
      part of the README.
    </p>--%>

    <p>
      <forms:checkbox
        name="dingNoticeEnabled"
        checked="${dingNoticeEnabled}"
        onclick="$('dingNoticeConfig').hidden = !this.checked"/>

      <label for="dingNoticeEnabled">启用钉钉机器人插件</label>
    </p>

    <table id="dingNoticeConfig" class="runnerFormTable" ${dingNoticeEnabled ? '' : 'hidden'}>
      <tr>
        <th class="noBorder"><label for="url">钉钉机器人地址: &nbsp;<span class="mandatoryAsterix" title="Mandatory field">*</span></label></th>
        <td class="noBorder">
          <forms:textField maxlength="300" name="url" value="${url}" />
          <%--<span class="smallNote">
            Property name for the project, or leave blank to use the actual project name.
            Use no prefix for configuration parameters, 'env.' for environment variables
            and 'system.' for system properties.
          </span>--%>
          <span id="errorSpanUrl" hidden class="error">输入有误，请输入正确的http地址</span>
        </td>
      </tr>

      <tr class="groupingTitle">
        <td>安全参数设置</td>
        <td><span id="errorSpanSafeParams" hidden class="error">输入有误，三者需填其一</span></td>
      </tr>

      <tr>
        <th><label for="keyword">自定义关键字:</label></th>
        <td>
          <forms:textField maxlength="100" name="keyword" value="${keyword}" />
        </td>
      </tr>

      <tr>
        <th><label for="sign">加签:</label></th>
        <td>
          <forms:textField maxlength="100" name="sign" value="${sign}" />
        </td>
      </tr>

      <tr>
        <th><label for="ipAddress">IP地址 (段):</label></th>
        <td>
          <forms:textField maxlength="100" name="ipAddress" value="${ipAddress}" />
          <span id="errorSpanIpAddress" hidden class="error">输入有误，请输入正确的IP地址</span>
        </td>
      </tr>

      <%--<tr>
        <td><label for="versionKey">Version Property:</label></td>
        <td>
          <forms:textField maxlength="100" name="versionKey" value="${versionKey}"/>
          <span class="smallNote">
            Property name for the version of the build being deployed, or blank to use the
            TeamCity build number. Use no prefix for configuration parameters, 'env.' for environment
            variables and 'system.' for system properties.
          </span>
        </td>
      </tr>

      <tr>
        <td><label for="environmentKey">Environment Property:</label></td>
        <td>
          <forms:textField maxlength="100" name="environmentKey" value="${environmentKey}"/>
          <span class="smallNote">
            Property name for the environment where the build was deployed, or blank to use the
            name of the build configuration. Use no prefix for configuration parameters,
            'env.' for environment variables and 'system.' for system properties.
          </span>
        </td>
      </tr>

      <tr>
        <td><label for="environments">Environments:</label></td>
        <td>
          <forms:textField maxlength="500" name="environments" value="${environments}" className="longField"/>
          <span class="smallNote">
            Comma separated and ordered list of environments being deploy to.
            Deployment builds not in this list will not be shown.
          </span>
        </td>
      </tr>

      <tr>
        <td><label for="customKey">Custom Property:</label></td>
        <td>
          <forms:textField maxlength="100" name="customKey" value="${customKey}"/>
          <span class="smallNote">
            Property name to show bespoke information with each deployment, such as the branch name
            (teamcity.build.branch). Leave blank if no information is required.
            Use no prefix for configuration parameters, 'env.' for environment variables and 'system.'
            for system properties.
          </span>
        </td>
      </tr>

      <tr>
        <td><label for="refreshSecs">Refresh Interval:</label></td>
        <td>
          <forms:select name="refreshSecs" enableFilter="true" className="smallField">
            <forms:option selected="${refreshSecs.isEmpty()}" value="">No Refresh</forms:option>
            <forms:option selected="${refreshSecs == '5'}" value="5">5 seconds</forms:option>
            <forms:option selected="${refreshSecs == '10'}" value="10">10 seconds</forms:option>
            <forms:option selected="${refreshSecs == '30'}" value="30">30 seconds</forms:option>
            <forms:option selected="${refreshSecs == '60'}" value="60">60 seconds</forms:option>
          </forms:select>
          <span class="smallNote">
            Polling interval between fetching new deployment build information, in seconds.
          </span>
        </td>
      </tr>

      <tr>
        <td>Build Scanning:</td>
        <td>
          <forms:checkbox
                  name="multiEnvConfig"
                  checked="${multiEnvConfig}"/>
          <label for="multiEnvConfig">Multi-Environment Build Configurations</label>
          <span class="smallNote">
                Enable this option only if deploying to more than one environment from the same build
                configuration. This requires a deeper scan of the build history and so may cause
                performance issues. The plugin will stop scanning a build configuration when deployments
                for all configured environments have been found, which should mitigate most issues.
          </span>
        </td>
      </tr>--%>
    </table>

    <div class="saveButtonsBlock">
      <forms:submit label="Save" />
        <input type="hidden" id="projectExternalId" name="projectExternalId" value="${projectExternalId}"/>
      <forms:saving />
    </div>
  </div>
</form>
