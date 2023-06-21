package dacs.nguyenhuubang.bookingwebsiteV1;



/*@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)*/
public class UserRepositoryTest {
  /*  @Autowired
    private UserRepository userRepo;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private TestEntityManager entityManager;

    private UserEntity getUser(){
        UserEntity user =new UserEntity();
        user.setEmail("nghgbanggads@gmail.com");
        user.setFullname("Nguyen Van A");
        user.setAddress("Tây Ninh");
        user.setPassword("111111");
        user.setEnabled(false);
        user.setRole("USER");
        return user;
    }

    @Test
    public void testAddNew(){
        UserEntity user = getUser();

        UserEntity savedUser = userRepo.save(user);
        UserEntity existUser = entityManager.find(UserEntity.class, savedUser.getId());

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isGreaterThan(0);
        assertThat(user.getEmail()).isEqualTo(existUser.getEmail());
    }



    @Test
    public void testFindAll() {
        UserEntity user =getUser();
        userRepo.save(user);
        List<UserEntity> result = new ArrayList<>();
        userRepo.findAll().forEach(e -> result.add(e));
        assertEquals(result.size(), 1);
    }
*//*    @Test
    public void testFindUser() {
        List<UserEntity> result = new ArrayList<>();
        userRepo.search("B").forEach(e -> result.add(e));
        assertEquals(result.size(), 1);
    }*//*
    @Test
    public void testFindById() {
        UserEntity employee = getUser();
        userRepo.save(employee);
        UserEntity result = userRepo.findById(employee.getId()).get();
        assertEquals(employee.getId(), result.getId());
    }
    @Test
    public void testDeleteById() {
        UserEntity employee = getUser();
        userRepo.save(employee);
        userRepo.deleteById(employee.getId());
        List<UserEntity> result = new ArrayList<>();
        userRepo.findAll().forEach(e -> result.add(e));
        assertEquals(result.size(), 0);
    }

    @Test
    public void testUpdate(){
        Integer userId =7;
        UserEntity user = userRepo.findById(userId).get();
        user.setFullname("Not U");
        userRepo.save(user);

        UserEntity userUpdate = userRepo.findById(userId).get();
        assertEquals(userUpdate.getFullname(), "Not U");
    }

    @Test
    public void testGet(){
        Integer userId =7;
        Optional<UserEntity> user = userRepo.findById(userId);
        assertThat(user).isPresent();
        System.out.println(user.get());
    }
*/
}
