Feature: Get all List Quotes


  Scenario Outline: Verify the List Quotes
    Given fav Quotes <filter>, <value>, <valueexpected>
    Examples:
      | filter    | value           | valueexpected |
      | "text"    | "funny"         | "true"        |
      | "author"  | "Neeraj Singla" | "true"        |
      | "author"  | "Mohnish"       | "false"       |
      | "author"  | "2332"          | "false"       |
      | "tags"    | "funny"         | "true"        |
      | "private" | ""              | "false"       |


