package com.mapr.springframework.data.maprdb.functional;

import com.google.common.collect.Lists;
import com.mapr.springframework.data.maprdb.functional.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.mapr.springframework.data.maprdb.utils.UserUtils.LIST_SIZE;

public class PageableAndSortTests extends AbstractFunctionalTests {

    //TODO Test for queries with over 5000 records
    @Test
    public void SortTest() {
        List<User> usersFromDB = repository.findAll(new Sort(Sort.Direction.DESC,"name"));

        List<User> sortedUsers = Lists.reverse(users.stream().sorted(Comparator.comparing(User::getName))
                .collect(Collectors.toList()));

        Assert.assertEquals(sortedUsers.size(), usersFromDB.size());

        Assert.assertEquals(sortedUsers, usersFromDB);
    }

    //TODO Test for queries with over 5000 records
    @Test
    public void MultipleSortTest() {
        List<User> usersFromDB = repository.findAll(new Sort(Sort.Direction.ASC,"name", "_id"));

        List<User> sortedUsers = users.stream().sorted(Comparator.comparing(User::getName).thenComparing(User::getId))
                .collect(Collectors.toList());

        Assert.assertEquals(sortedUsers.size(), usersFromDB.size());

        Assert.assertEquals(sortedUsers, usersFromDB);
    }

    @Test
    public void PageableTest() {
        int usersPerPage = 10;
        int pageNumber = 1;
        Page<User> pagedUsers = repository.findAll(PageRequest.of(pageNumber, usersPerPage));


        Assert.assertEquals(usersPerPage, pagedUsers.getSize());

        Assert.assertEquals(LIST_SIZE, pagedUsers.getTotalElements());

        Assert.assertEquals(LIST_SIZE / usersPerPage, pagedUsers.getTotalPages());

        List<User> filteredUsers = users.stream().sorted(Comparator.comparing(User::getId))
                .skip(pageNumber * usersPerPage).limit(usersPerPage).collect(Collectors.toList());

        Assert.assertEquals(filteredUsers, pagedUsers.getContent());
    }

    @Test
    public void PageableWithSortTest() {
        int usersPerPage = 20;
        int pageNumber = 2;
        Page<User> pagedUsers = repository.findAll(
                PageRequest.of(pageNumber, usersPerPage, Sort.Direction.ASC, "name"));

        Assert.assertEquals(usersPerPage, pagedUsers.getSize());

        Assert.assertEquals(LIST_SIZE, pagedUsers.getTotalElements());

        Assert.assertEquals(LIST_SIZE / usersPerPage, pagedUsers.getTotalPages());

        List<User> filteredUsers = users.stream().sorted(Comparator.comparing(User::getName))
                .skip(pageNumber * usersPerPage).limit(usersPerPage).collect(Collectors.toList());

        Assert.assertEquals(filteredUsers, pagedUsers.getContent());
    }

    @Test
    public void firstTest() {
        int expectedAmount = 10;
        List<User> usersFromDB = repository.findFirst10ByEnabledFalse();

        Assert.assertEquals(expectedAmount, usersFromDB.size());

        List<User> expectedUsers = users.stream().sorted(Comparator.comparing(User::getId)).limit(expectedAmount)
                .collect(Collectors.toList());

        Assert.assertEquals(expectedUsers, usersFromDB);
    }

    @Test
    public void topTest() {
        int expectedAmount = 10;
        List<User> usersFromDB = repository.findTop10ByEnabledFalse();

        Assert.assertEquals(expectedAmount, usersFromDB.size());

        List<User> expectedUsers = users.stream().sorted(Comparator.comparing(User::getId))
                .skip(LIST_SIZE - expectedAmount).collect(Collectors.toList());

        Assert.assertEquals(expectedUsers, usersFromDB);
    }

    @Test
    public void pageableTest() {
        int expectedPage = 2;
        int expectedNumberOfUsers = 13;

        List<User> usersFromDB = repository.findByEnabledFalse(PageRequest.of(expectedPage, expectedNumberOfUsers));

        Assert.assertEquals(expectedNumberOfUsers, usersFromDB.size());

        List<User> expectedUsers = users.stream().sorted(Comparator.comparing(User::getId))
                .skip(expectedPage * expectedNumberOfUsers).limit(expectedNumberOfUsers).collect(Collectors.toList());

        Assert.assertEquals(expectedUsers, usersFromDB);
    }

}
