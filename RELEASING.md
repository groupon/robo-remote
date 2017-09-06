Releasing
========

### the release process

We release on the master branch, all changes have to be merged.

1. check `mvn clean install` works well
2. type `mvn clean release:prepare`
3. type `mvn clean release:perform`

If step 3 to X fails, type `mvn release:rollback` followed by `mvn release:clean`. A tag might have been pushed and will need to be erased manually on the remote repo, or this follow the instruction at https://nathanhoad.net/how-to-delete-a-remote-git-tag.