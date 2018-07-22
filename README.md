
current core commands are:

__!set <command_name> <command_code>__  
&nbsp;&nbsp;creates or overwrites command (mods & whitelist only)  
__!permit <user_name>__  
&nbsp;&nbsp;adds <user_name> to the whitelist (mods only)  
__!unpermit <user_name>__  
&nbsp;&nbsp;removes <user_name> from the whitelist (mods only)  
__!alias <alias_name> [command_name]__  
&nbsp;&nbsp;creates a new alias or removes it if [command_name] is empty (mods & whitelist only)  
__!tag <command_name> <tag_name>__  
&nbsp;&nbsp;adds the <tag_name> tag to the command <command_name> (mods & whitelist only)  
__!untag <command_name> <tag_name>__  
&nbsp;&nbsp;removes the <tag_name> tag from the command <command_name> (mods & whitelist only)  
__!list <tag_name>__  
&nbsp;&nbsp;lists all commands with the tag <tag_name>  
__!show <command_name>__  
&nbsp;&nbsp;displays the code of the command <command_name> (mods & whitelist only)  
__!tags__  
&nbsp;&nbsp;lists all tags


Command codes may contain tags which will be evaluated when running the command,  
following tags may currently be used in command codes:

__[time]<timestamp>[/time]__  
&nbsp;&nbsp;returns the duration elapsed since <timestamp>, eg. 1day 3sec  
__[now][/now]__  
&nbsp;&nbsp;returns current <timestamp>  
__[sender][/sender]__  
&nbsp;&nbsp;returns the <user_name> of the user who sent the command message  
__[bracket][/bracket]__  
&nbsp;&nbsp;returns the literal character '[', intended for escaping  
__[math]<expression>[/math]__  
&nbsp;&nbsp;returns the computed result of the simple math expression <expression>, eg. '40 + 2' returns '42'  
__[echo]<message>[/echo]__  
&nbsp;&nbsp;returns nothing, the bot will send a seperate twitch-message with the text <message>  
__[regex]<regular expression>[/regex]__  
&nbsp;&nbsp;returns the first match from matching <regular expression> on the root-command arguments  
__[cmd]<command_name> <command_arguments>[/cmd]__  
&nbsp;&nbsp;returns the result of the evaluated given command  

all core commands can also be added as tags, eg:  
[set]<command_name> <command_code>[/set]  
permission filters still apply
