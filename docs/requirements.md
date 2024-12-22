A common problem when working with coding assitants like Cline is they need to manually run file searches
through the code to better understand the codebase.

This can be slow and tedious.

Also, sometimes the developer wants to ask questions about the overall code base. Some example quetsions
include:

- Please summarize the key features and functionality of this codebase
- Write a high level design document for this codebase, using object and sequence diagrams where useful
- Write a summary of the key components of this codebase, with a paragraph or two for each component
- What are some of the more problematic files, applying SOLID and clean coding principles

I would like to create an MCP plugin that Cline can use which allows Cline to easily ask questions about
the codebase. This plugin could also be used by Claude Desktop so the developer can ask questions directly.