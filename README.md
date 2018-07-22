
current core commands are:

__!set &lt;command_name> &lt;command_code>__  
&nbsp;&nbsp;creates or overwrites command (mods & whitelist only)  
__!permit &lt;user_name>__  
&nbsp;&nbsp;adds &lt;user_name> to the whitelist (mods only)  
__!unpermit &lt;user_name>__  
&nbsp;&nbsp;removes &lt;user_name> from the whitelist (mods only)  
__!alias &lt;alias_name> [command_name]__  
&nbsp;&nbsp;creates a new alias or removes it if [command_name] is empty (mods & whitelist only)  
__!tag &lt;command_name> &lt;tag_name>__  
&nbsp;&nbsp;adds the &lt;tag_name> tag to the command &lt;command_name> (mods & whitelist only)  
__!untag &lt;command_name> &lt;tag_name>__  
&nbsp;&nbsp;removes the &lt;tag_name> tag from the command &lt;command_name> (mods & whitelist only)  
__!list &lt;tag_name>__  
&nbsp;&nbsp;lists all commands with the tag &lt;tag_name>  
__!show &lt;command_name>__  
&nbsp;&nbsp;displays the code of the command &lt;command_name> (mods & whitelist only)  
__!tags__  
&nbsp;&nbsp;lists all tags


Command codes may contain tags which will be evaluated when running the command,  
following tags may currently be used in command codes:

__[time]&lt;timestamp>[/time]__  
&nbsp;&nbsp;returns the duration elapsed since &lt;timestamp>, eg. 1day 3sec  
__[now][/now]__  
&nbsp;&nbsp;returns current &lt;timestamp>  
__[sender][/sender]__  
&nbsp;&nbsp;returns the &lt;user_name> of the user who sent the command message  
__[bracket][/bracket]__  
&nbsp;&nbsp;returns the literal character '[', intended for escaping  
__[math]&lt;expression>[/math]__  
&nbsp;&nbsp;returns the computed result of the simple math expression &lt;expression>, eg. '40 + 2' returns '42'  
__[echo]&lt;message>[/echo]__  
&nbsp;&nbsp;returns nothing, the bot will send a seperate twitch-message with the text &lt;message>  
__[regex]&lt;regular expression>[/regex]__  
&nbsp;&nbsp;returns the first match from matching &lt;regular expression> on the root-command arguments  
__[cmd]&lt;command_name> &lt;command_arguments>[/cmd]__  
&nbsp;&nbsp;returns the result of the evaluated given command  

all core commands can also be added as tags, eg:  
[set]&lt;command_name> &lt;command_code>[/set]  
permission filters still apply
