import { test, expect } from '@playwright/test';

// Login valid
test("should login successfully with valid credentials", async ({ page }) => {
  await page.goto("https://www.saucedemo.com");

  await page.fill("#user-name", "standard_user");
  await page.fill("#password", "secret_sauce");
  await page.click("#login-button");

  await expect(page).toHaveURL(/.*inventory.html/);
});

// Login invalid
const invalidCredentials = [
  { username: "standard_user", password: "wrong_password" },
  { username: "wrong_user", password: "secret_sauce" },
  { username: "", password: "secret_sauce" },
  { username: "standard_user", password: "" },
  { username: "", password: "" },
  { username: "locked_out_user", password: "secret_sauce" },
];

for (const creds of invalidCredentials) {
  test(`should NOT login with username="${creds.username}" and password="${creds.password}"`, async ({
    page,
  }) => {
    await page.goto("https://www.saucedemo.com");

    await page.fill("#user-name", creds.username);
    await page.fill("#password", creds.password);
    await page.click("#login-button");

    const errorMessage = page.locator('[data-test="error"]');
    await expect(errorMessage).toBeVisible();
  });
}

// Testare flow
test("user flow: login, add to cart, remove from cart, logout", async ({
  page,
}) => {
  await page.goto("https://www.saucedemo.com");

  await page.fill("#user-name", "standard_user");
  await page.fill("#password", "secret_sauce");
  await page.click("#login-button");

  await expect(page).toHaveURL(/.*inventory.html/);

  const firstAddToCartButton = page
    .locator('button[data-test^="add-to-cart"]')
    .first();
  await firstAddToCartButton.click();

  const cartBadge = page.locator(".shopping_cart_badge");
  await expect(cartBadge).toHaveText("1");

  await page.click(".shopping_cart_link");
  await expect(page).toHaveURL(/.*cart.html/);

  const removeButton = page.locator('button[data-test^="remove"]').first();
  await removeButton.click();

  await expect(page.locator(".cart_item")).toHaveCount(0);

  await page.click("#react-burger-menu-btn");
  await page.click("#logout_sidebar_link");

  await expect(page).toHaveURL("https://www.saucedemo.com/");
});
