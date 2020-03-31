private  int pattern(StringBuffer sb) {
        int sum = 0;
        Pattern pattern = Pattern.compile("[a-zA-Z']+");
        Matcher matcher = pattern.matcher(sb);
        while(matcher.find()) {
            sum++;
        }
        return sum;
        //统计单词