def asInt(value, defaultValue=0){
    return value ? value.toInteger() : defaultValue
}
def asBoolean(value, defaultValue=false){
    return value != null ? value.toBoolean() : defaultValue
}

def setup(config) {
    config = config ?: [:]
    def desc = jenkins.model.Jenkins.instance.getDescriptor(hudson.plugins.jira.JiraProjectProperty)
    def sites = config.sites?.collect{ siteConfig ->
        siteConfig.with{
            def site
            if(username && password){
                site = new hudson.plugins.jira.JiraSite(
                    url ? new URL(url) : null,
                    alternativeUrl ? new URL(alternativeUrl) : null,
                    username,
                    password,
                    asBoolean(supportsWikiStyleComment),
                    asBoolean(recordScmChanges),
                    userPattern,
                    asBoolean(updateJiraIssueForAllStatus),
                    groupVisibility,
                    roleVisibility,
                    asBoolean(useHTTPAuth)
                )
            }else{
                site = new hudson.plugins.jira.JiraSite(
                    url ? new URL(url) : null,
                    alternativeUrl ? new URL(alternativeUrl) : null,
                    credentialsId,
                    asBoolean(supportsWikiStyleComment),
                    asBoolean(recordScmChanges),
                    userPattern,
                    asBoolean(updateJiraIssueForAllStatus),
                    groupVisibility,
                    roleVisibility,
                    asBoolean(useHTTPAuth)
                )
            }
            site.disableChangelogAnnotations = asBoolean(disableChangelogAnnotations)
            site.timeout = asInt(timeout, hudson.plugins.jira.JiraSite.DEFAULT_TIMEOUT)
            site.readTimeout = asInt(readTimeout, hudson.plugins.jira.JiraSite.DEFAULT_READ_TIMEOUT)
            site.dateTimePattern = dateTimePattern
            site.appendChangeTimestamp = asBoolean(appendChangeTimestamp, null)
            return site
        }
    }
    if(sites){
        def formData = [:] as net.sf.json.JSONObject
        def req = [
            bindJSONToList: {clz, obj -> return sites}
        ] as org.kohsuke.stapler.StaplerRequest
        desc.configure(req, formData)
    }
}
return this
