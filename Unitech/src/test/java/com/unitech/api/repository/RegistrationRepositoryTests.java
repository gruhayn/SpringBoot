package com.unitech.api.repository;

import com.unitech.api.constants.Constants;
import com.unitech.api.models.UserEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Optional;


@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RegistrationRepositoryTests {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Test
    public void RegistrationRepository_GetAll_ReturnMoreThenOneRegistration() {
        UserEntity entity1 = UserEntity.builder()
                .pin("pin1")
                .password("password1").build();

        UserEntity entity2 = UserEntity.builder()
                .pin("pin2")
                .password("password2").build();

        registrationRepository.save(entity1);
        registrationRepository.save(entity2);

        List<UserEntity> registrationList = registrationRepository.findAll();

        Assertions.assertThat(registrationList).isNotNull();
        Assertions.assertThat(registrationList.size()).isEqualTo(2);
    }

    @Test
    public void RegistrationRepository_GetAllWithPageablePageNo0PageSize1_ReturnOneRegistration() {
        UserEntity entity1 = UserEntity.builder()
                .pin("pin1")
                .password("password1").build();

        UserEntity entity2 = UserEntity.builder()
                .pin("pin2")
                .password("password2").build();

        registrationRepository.save(entity1);
        registrationRepository.save(entity2);

        int pageNo = 0;
        int pageSize = 1;
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        List<UserEntity> registrationList = registrationRepository.findAll(pageable).getContent();

        Assertions.assertThat(registrationList).isNotNull();
        Assertions.assertThat(registrationList.size()).isEqualTo(1);
    }

    @Test
    public void RegistrationRepository_GetAllWithPageablePageNo1PageSize1_ReturnOneRegistration() {
        UserEntity entity1 = UserEntity.builder()
                .pin("pin1")
                .password("password1").build();

        UserEntity entity2 = UserEntity.builder()
                .pin("pin2")
                .password("password2").build();

        registrationRepository.save(entity1);
        registrationRepository.save(entity2);

        int pageNo = 1;
        int pageSize = 1;
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        List<UserEntity> registrationList = registrationRepository.findAll(pageable).getContent();

        Assertions.assertThat(registrationList).isNotNull();
        Assertions.assertThat(registrationList.size()).isEqualTo(1);
    }

    @Test
    public void RegistrationRepository_GetAllWithPageablePageNo1PageSize2_ReturnMoreThenOneRegistration() {
        UserEntity entity1 = UserEntity.builder()
                .pin("pin1")
                .password("password1").build();

        UserEntity entity2 = UserEntity.builder()
                .pin("pin2")
                .password("password2").build();

        registrationRepository.save(entity1);
        registrationRepository.save(entity2);

        int pageNo = 0;
        int pageSize = 2;
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        List<UserEntity> registrationList = registrationRepository.findAll(pageable).getContent();

        Assertions.assertThat(registrationList).isNotNull();
        Assertions.assertThat(registrationList.size()).isEqualTo(2);
    }

    @Test
    public void RegistrationRepository_SaveAll_ReturnSavedRegistration() {

        //Arrange
        UserEntity userEntity = UserEntity.builder()
                .pin("pin3")
                .password("password3")
                .build();

        //Act
        UserEntity saved = registrationRepository.save(userEntity);

        //Assert
        Assertions.assertThat(saved).isNotNull();
        Assertions.assertThat(saved.getId()).isGreaterThan(0);
    }

    @Test
    public void RegistrationRepository_SaveAllUpdateCase_ReturnSavedRegistration() {

        //Arrange
        UserEntity userEntity = UserEntity.builder()
                .pin("pin3")
                .password("password3")
                .build();

        //Act
        UserEntity saved = registrationRepository.save(userEntity);
        userEntity = saved;
        saved = registrationRepository.save(userEntity);

        //Assert
        Assertions.assertThat(saved).isNotNull();
        Assertions.assertThat(saved.getId()).isGreaterThan(0);
        Assertions.assertThat(saved.getId()).isEqualTo(userEntity.getId());
    }

    @Test
    public void RegistrationRepository_ExistsByPin_ReturnsFalse() {

        //Arrange
        UserEntity entity = UserEntity.builder()
                .pin("pin1")
                .password("password2").build();

        //Act
        Boolean result = registrationRepository.existsByPin(entity.getPin());

        //Assert
        Assertions.assertThat(result).isEqualTo(false);
    }

    @Test
    public void RegistrationRepository_ExistsByPinAfterCreate_ReturnsTrue() {

        //Arrange
        UserEntity entity = UserEntity.builder()
                .pin("pin1")
                .password("password2").build();

        //Act
        UserEntity saved = registrationRepository.save(entity);
        Boolean result = registrationRepository.existsByPin(entity.getPin());
        //Assert
        Assertions.assertThat(result).isEqualTo(true);
    }


    @Test
    public void RegistrationRepositoty_FindByPinAndPassword_ExistReturnsRegistration(){
        UserEntity entity = UserEntity.builder()
                .pin("pin2")
                .password("password2").build();

        registrationRepository.save(entity);

        Optional<UserEntity> user = registrationRepository
                .findByPin(entity.getPin());

        Assertions.assertThat(user).isPresent();
    }

    @Test
    public void RegistrationRepositoty_FindByPinAndPassword_DoesntExistReturnsEmpty(){
        UserEntity entity = UserEntity.builder()
                .pin("pin2")
                .password("password2").build();


        Optional<UserEntity> user = registrationRepository
                .findByPin(entity.getPin());

        Assertions.assertThat(user).isEmpty();
    }


    @Test
    public void RegistrationRepositoty_FindByStatusId_DoesntExistReturnsEmpty(){
        UserEntity entity = UserEntity.builder()
                .pin("pin2")
                .password("password2").build();


        Page<UserEntity> user = registrationRepository
                .findByStatusId(Constants.UserStatuses.Active.id, Pageable.unpaged());

        Assertions.assertThat(user).isEmpty();
    }

    @Test
    public void RegistrationRepositoty_FindByStatusId_ExistReturnsActives(){
        UserEntity entity = UserEntity.builder()
                .pin("pin2")
                .password("password2")
                .statusId(Constants.UserStatuses.Active.id).build();


        registrationRepository.save(entity);

        Page<UserEntity> user = registrationRepository
                .findByStatusId(Constants.UserStatuses.Active.id, Pageable.ofSize(10).withPage(0));

        Assertions.assertThat(user).isNotEmpty();
    }



}
